<div id="search-result">
  {{#sub_cnt}}
    <ul class="subs">
      {{#subs}}
        <li class="ficon-error {{ cls }}">
          <a href="#{{href}}">
            {{#is_group}}
              <i class="icon-folder-open"></i>
            {{/is_group}}
            {{^is_group}}
            <img src="{{img}}" width="15" height="15" />
            <i class="icon-rss"></i>
            {{/is_group}}
            <span class="title">{{{title}}}</span>
            <span class="count">
              {{#dislike}}
                <span class="unread-dislike"
                  title="dislike count">{{dislike}}</span>
              {{/dislike}}
              {{#neutral}}
                <span class="unread-neutral"
                  title="neutral count">{{neutral}}</span>
              {{/neutral}}
              {{#like}}
                <span class="unread-like"
                  title="like count">{{like}}</span>
              {{/like}}
              <span class="total" title="total feed">{{ total }}</span>
            </span>
          </a>
        </li>
      {{/subs}}
    </ul>
  {{/sub_cnt}}
  <ul class="feeds">
    {{#feeds}}
      <li class="feed {{cls}}">
        <a href="#{{href}}">
          <span class="indicator"></span>
          <span class="title" title="{{{title}}}">{{{title_h}}}</span>
          {{#sub}}
            <span class="sub" title="from">{{ title }}</span>
          {{/sub}}
        </a>
      </li>
    {{/feeds}}
  </ul>
</div>
