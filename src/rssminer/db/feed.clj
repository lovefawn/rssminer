(ns rssminer.db.feed
  (:use [rssminer.db.util :only [h2-query id-k with-h2 h2-insert]]
        (rssminer [database :only [h2-db-factory]]
                  [search :only [index-feed]]
                  [time :only [now-seconds]]
                  [util :only [ignore-error]])
        [clojure.string :only [blank?]]
        [clojure.tools.logging :only [info trace]]
        [clojure.java.jdbc :only [with-connection insert-record
                                  update-values delete-rows]]))

(defn insert-pref [user-id feed-id pref]
  (with-h2
    (try (insert-record :user_feed {:user_id user-id
                                    :feed_id feed-id
                                    :pref pref})
         (catch Exception e             ;unique key use_id & feed_id
           (update-values :user_feed
                          ["user_id = ? AND feed_id = ?" user-id feed-id]
                          {:pref pref})))))

(defn save-feeds [feeds rss-id]
  (doseq [{:keys [link] :as feed} (:entries feeds)]
    (when (and link (not (blank? link)))
      (try
        (let [id (id-k (with-h2
                         (insert-record :feeds
                                        (assoc feed
                                          :rss_link_id rss-id))))]
          (index-feed id feed))
        ;; TODO (link, rss_link_id) is uniqe
        (catch RuntimeException e       ;link is uniqe
          ;; (trace "update" link)
          (with-connection @h2-db-factory
            (update-values :feeds ["link=?" link] feed)))))))

(defn fetch-by-rssid [user-id rss-id limit offset]
  (h2-query ["SELECT f.id, author, link, title, tags,
                     published_ts, uf.read
              FROM feeds f LEFT OUTER JOIN user_feed uf ON uf.feed_id = f.id
              WHERE rss_link_id = ? AND
             (uf.user_id = ? or uf.user_id IS NULL) LIMIT ? OFFSET ?"
             rss-id user-id limit offset] :convert))

(defn fetch-by-id [id]
  (first (h2-query ["SELECT * FROM feeds WHERE id = ?" id] :convert)))

(defn fetch-orginal [id]
  (first
   (h2-query ["SELECT original, link FROM feeds WHERE id = ?" id] :convert)))

(defn fetch-unread-meta [user-id]
  (h2-query ["SELECT c.* FROM (
       SELECT f.id as f_id, us.rss_link_id, f.published_ts FROM
       feeds f JOIN user_subscription us ON us.rss_link_id = f.rss_link_id
       WHERE us.user_id = ? ) AS c
       LEFT OUTER JOIN user_feed uf ON uf.feed_id = c.f_id
       WHERE (uf.read = FALSE OR uf.read IS NULL)" user-id]))

(defn fetch-unread [user-id limit offset]
  (h2-query ["SELECT c.* FROM (
       SELECT f.id, f.author, f.link, f.title, f.tags,f.published_ts FROM
       feeds f JOIN user_subscription us ON us.rss_link_id = f.rss_link_id
       WHERE us.user_id = ? ) AS c
       LEFT OUTER JOIN user_feed uf ON uf.feed_id = c.id
       WHERE (uf.read = FALSE OR uf.read IS NULL)
       LIMIT ? offset ?" user-id limit offset]))

(defn- safe-update-rss-link [id data]
  (with-h2
    (update-values :rss_links ["id = ?" id] data)))

(defn update-rss-link [id data]
  (if-let [url (:url data)]
    (if-let [saved-id (-> (h2-query ["select id from rss_links where url = ?"
                                     (:url data)]) first :id)]
      (do
        (with-h2
          (update-values :user_subscription ["rss_link_id = ?" id]
                         {:rss_link_id saved-id})
          (update-values :feeds ["rss_link_id = ?" id]
                         {:rss_link_id saved-id})
          (delete-rows :rss_links ["id = ?" id])))
      (safe-update-rss-link id data))
    (safe-update-rss-link id data)))

(defn save-feed-original [id original]
  (with-h2 (update-values :feeds ["id = ?" id] {:original original})))

(defn fetch-rss-links [limit]           ; for fetcher
  "Returns nil when no more"
  (h2-query ["SELECT id, url, check_interval, last_modified
              FROM rss_links
              WHERE next_check_ts < ?
              ORDER BY next_check_ts LIMIT ?" (now-seconds) limit]))

(defn insert-rss-link
  [link]
  (ignore-error ;; ignore voilate of uniqe constraint
   (with-h2 (insert-record :rss_links link))))
