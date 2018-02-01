var clyKey = '';

if (clyKey) {
  var Countly = Countly || {};
  Countly.q = Countly.q || [];

  Countly.app_key = clyKey;
  Countly.url = 'https://restcomm.count.ly';

  Countly.q.push(['track_sessions']);
  Countly.q.push(['track_pageview', location.pathname + location.hash]);

  $(window).on('hashchange', function() {
    Countly.q.push(['track_pageview',location.pathname + location.hash]);
  });

  Countly.q.push(['track_clicks']);
  Countly.q.push(['track_links']);
  Countly.q.push(['track_forms']);
  Countly.q.push(['collect_from_forms']);
  Countly.q.push(['report_conversion']);

  // load countly script asynchronously
  (function() {
    var cly = document.createElement('script');
    cly.type = 'text/javascript';
    cly.async = true;
    cly.src = 'https://restcomm.count.ly/sdk/web/countly.min.js';
    cly.onload = function(){ Countly.init() };
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(cly, s);
  })();


  var myEvents = [
    {id: 'designer-diagram-toggle', trigger: 'click', cly: {key: 'designer_diagram_toggle', ngSeg: "{status: showGraph}" } },
    {id: 'designer-module-add', trigger: 'click', cly: {key: 'designer_module_add'} },
    {id: 'voice-modules-listing', trigger: 'sortreceive', cly: {key: 'designer_verb_dropped', fnSeg: function (verbName) { return {verb: verbName, kind: 'voice'}}}},
    {id: 'sms-modules-listing', trigger: 'sortreceive', cly: {key: 'designer_verb_dropped', fnSeg: function (verbName) { return {verb: verbName, kind: 'sms'}}}},
    {id: 'ussd-modules-listing', trigger: 'sortreceive', cly: {key: 'designer_verb_dropped', fnSeg: function (verbName) { return {verb: verbName, kind: 'ussd'}}}},
    {id: 'gather-nested-listing', trigger: 'sortreceive', cly: {key: 'designer_verb_dropped', fnSeg: function (verbName) { return {verb: verbName, kind: 'voice'}}}},
    {id: 'wt-save-button', trigger: 'click', cly: {key: 'designer-wt-update', ngSeg: "{active: !!ccInfo}"}},
    {id: 'wt-cancel-button', trigger: 'click', cly: {key: 'designer-wt-cancel'}},
    {id: 'wt-show-button', trigger: 'click', cly: {key: 'designer-wt-show'}},
    {id: 'log-open-button', trigger: 'click', cly: {key: 'designer-log-open'}},
    {id: 'idesettings-save-button', trigger: 'click', cly: {key: 'designer-idesettings-update'}},
    {id: 'rvd-designer', trigger: 'ngevent', ngEvents: [
      // 'name' has the angular event identifier
      {name: 'mediafile-uploaded', cly: {key: 'designer_media_uploaded', fnSeg: function (eventData) { var match=/\.[^\.]+/.exec(eventData.filename); return {ext: match ? match[0] : undefined } }}},
    ]},
    {id: 'authenticated-user-menu', trigger: 'nglink', fn: function (scope) {
      scope.$watch('authInfo', function (newAuthInfo) {
        if (!!newAuthInfo.username) {
          Countly.q.push(function() {
            Countly.user_details({name: newAuthInfo.username});
          });
        }
      });
    }}

  ];

  var clyReport = function (scope, elem, attrs) {

    function pushClyEvent(event, seg) {
      Countly.q.push(['add_event',{
        key: event.cly.key,
        count: 1,
        segmentation: seg
      }]);
    }

    angular.forEach(myEvents, function (event) {
      if (elem[0].id === event.id) {
        if (event.trigger === 'click') {
          elem.on('click', function () {
            var reportValue;
            if (event.cly.ngSeg) {
              reportValue = scope.$eval(event.cly.ngSeg);
            } else
            if (event.cly.seg) {
              reportValue = evaluate(event.cly.seg, elem);
            }
            pushClyEvent(event, reportValue);
          });
        }
        if (event.trigger === 'sortreceive') { // drag-n-drop stuff
          elem.bind("sortreceive", function (jqEvent, ui) {
            var match = /button-([^ ]*)/.exec(ui.sender[0].className)[1];
            if (typeof(match) !== undefined) {
              if (typeof(event.cly.fnSeg) === 'function') {
                pushClyEvent(event, event.cly.fnSeg(match));
              }
            }
          });
        }
        if (event.trigger === 'ngevent') { // when an angular event is $broadcasted/$on-ed
          angular.forEach(event.ngEvents, function (ngEvent) {
            scope.$on(ngEvent.name,function (eventInfo, eventData) {
              if (typeof(ngEvent.fnSeg) === 'function') {
                pushClyEvent(ngEvent, ngEvent.cly.fnSeg(eventData))
              }
            })
          });
        }
        if (event.trigger === 'nglink') { // when element directive is linked
          if (typeof(event.fn) === 'function') {
            event.fn(scope);
          }
        }
      }
    });

  };

  angular.module('Rvd').directive('button', function () {
    return {
      restrict: 'E',
      link: clyReport
    }});

  angular.module('Rvd').directive('a', function () {
    return {
      restrict: 'E',
      link: clyReport
    }
  });

  /*
    Custom directive that enables check against myEvents list for reporting analytics.
    When present, the element's id will be checked against myEvents[].id. This helps
    customize behavior for elements different than button/a
  */
  angular.module('Rvd').directive('clyReporter', function () {
    return {
      restrict: 'A',
      link: clyReport
    }
  });

}