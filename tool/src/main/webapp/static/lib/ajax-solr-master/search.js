/*
 * #%L
 * Course Signup Webapp
 * %%
 * Copyright (C) 2010 - 2013 University of Oxford
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
var Manager;

(function ($) {

  $(function () {
    Manager = new AjaxSolr.Manager({
      solrUrl: 'http://localhost:8983/solr/ses/'
    });

    Manager.addWidget(new AjaxSolr.ResultWidget({
      id: 'result',
      target: '#docs'
    }));
    Manager.addWidget(new AjaxSolr.PagerWidget({
      id: 'pager',
      target: '#pager',
      prevLabel: '&lt;',
      nextLabel: '&gt;',
      innerWindow: 1,
      renderHeader: function (perPage, offset, total) {
        $('#pager-header').html($('<span></span>').text('displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of ' + total));
      }
    }));
    
    var fields = [ 'provider_title', 'course_subject_rdf', 'course_subject_rm' ];
    for (var i = 0, l = fields.length; i < l; i++) {
      Manager.addWidget(new AjaxSolr.TagcloudWidget({
        id: fields[i],
        target: '#' + fields[i],
        field: fields[i]
      }));
    }
    
    Manager.addWidget(new AjaxSolr.CurrentSearchWidget({
        id: 'currentsearch',
        target: '#selection'
    }));
    
    Manager.addWidget(new AjaxSolr.TextWidget({
    	  id: 'text',
    	  target: '#search'
    }));
    /*
    Manager.addWidget(new AjaxSolr.AutocompleteWidget({
      id: 'text',
      target: '#search',
      fields: [ 'course_title', 'course_description' ]
    }));
    */
    Manager.init();
    Manager.store.addByValue('q', '*:*');
    
    var params = {
    		  group: true,
    		  'group.field': ['course_identifier'],
    		  'group.ngroups': true
    		};
    		for (var name in params) {
    		  Manager.store.addByValue(name, params[name]);
    		}
    
    var params = {
      facet: true,
      'facet.field': [ 'provider_title', 'course_subject_rdf', 'course_subject_rm' ],
      'facet.limit': 20,
      'facet.mincount': 1,
      'f.topics.facet.limit': 50
    };
    for (var name in params) {
      Manager.store.addByValue(name, params[name]);
    }
    
    Manager.doRequest();
  });
/*
  $.fn.showIf = function (condition) {
    if (condition) {
      return this.show();
    }
    else {
      return this.hide();
    }
  }
*/
})(jQuery);
