<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Rssminer, intelligent rss reader</title>
    <meta name="keywords" content="rss miner, rssminer, rss aggregator,
                                   intelligent rss reader">
    <meta name="description"
          content="RSSMiner is an intelligent web-based rss reader. By
                   machine learning, RSSMiner highlight stories you
                   like, and help discover stories you may like.">
    {{#prod}}<style type="text/css">{{{ css }}}</style>{{/prod}}
    {{#dev}}<link rel="stylesheet" href="/css/app.css">{{/dev}}
    {{{ data }}}
    <!--[if lt IE 8 ]><script>location.href="/browser"</script><![endif]-->
  </head>
  <body>
    <div id="header">
      <div class="wrapper">
        <div id="logo" class="show-nav">
          <h1><a href="#">Rssminer</a></h1>
          <ul id="sub-list"></ul>
        </div>
        <div id="warn-msg">
          You are viewing a demo account,
          <a href="/">create one for youself</a>
        </div>
        <input id="q" autocomplete="off"
               placeholder="type to search subscriptions, articles"/>
        <i class="icon-search"></i>
        <ul class="links">
          <li>
            <a href="#s/add" title="Add subscription" class="btn">
              <i class="icon-edit"></i><span>Add</span>
            </a>
          </li>
          <li>
            <a href="#s/account" title="Account settings">
              <i class="icon-user"></i>
            </a>
          </li>
          <li>
            <a href="#s/about" title="About">
              <i class="icon-info-sign"></i>
            </a>
          </li>
          <li>
            <a href="/logout" title="Logout">
              <i class="icon-off"></i>
            </a>
          </li>
        </ul>
      </div>
    </div>
    <div id="main">
      <div id="navigation">
        <ul id="feed-list" class="feeds"></ul>
      </div>
      <div id="content">
        <div id="reading-area">
          <div id="welcome-list"></div>
          <div class="iframe">
            <iframe src="about:blank"
                    sandbox="allow-scripts allow-same-origin"></iframe>
            <div id="footer"></div><!-- footer info -->
          </div>
        </div>
      </div>
    </div>
    <ul id="ct-menu"></ul>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    {{#dev}}
    <script src="/js/lib/jquery-ui-1.8.18.custom.js"></script>
    <script src="/js/lib/underscore.js"></script>
    <script src="/js/lib/mustache.js"></script>
    <script src="/js/gen/app-tmpls.js"></script>
    <script src="/js/rssminer/util.js"></script>
    <script src="/js/rssminer/ajax.js"></script>
    <script src="/js/rssminer/router.js"></script>
    <script src="/js/rssminer/layout.js"></script>
    <script src="/js/rssminer/rm_data.js"></script>
    <script src="/js/rssminer/search.js"></script>
    <script src="/js/rssminer/ct_menu.js"></script>
    <script src="/js/rssminer/app.js"></script>
    {{/dev}}
    {{#prod}}<script src="/js/app-min.js?{VERSION}"></script>{{/prod}}
  </body>
</html>
