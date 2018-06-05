angular.module('Rvd')
.service('notifications', ['$rootScope', '$timeout', function($rootScope, $timeout) {
	var notifications = {data:[]};

	$rootScope.notifications = notifications;

	notifications.put = function (notif) {
		notifications.data.push(notif);

		var timeout = 3000;
		if (typeof notif.timeout !== "undefined" )
		    timeout = notif.timeout;

        if (timeout > 0) {
            $timeout(function () {
                if (notifications.data.indexOf(notif) != -1)
                    notifications.data.splice(notifications.data.indexOf(notif),1);
            }, timeout);
		}
	}

	notifications.remove = function (removedIndex) {
		notifications.data.splice(removedIndex, 1);
	}

	notifications.clear = function () {
	    notifications.data = [];
	}

	return notifications;
}]);

angular.module('Rvd').service('storage',function ($sessionStorage) {
    function getCredentials() {
      var creds = {};
      creds.sid = sessionStorage.sid;
      creds.auth_token = sessionStorage.auth_token;
      creds.username = sessionStorage.username;
      return creds;
    }

    function setCredentials(username, token, sid) {
      sessionStorage.sid = sid;
      sessionStorage.auth_token = token;
      sessionStorage.username = username;
      //$sessionStorage.rvdCredentials = {username: username, token: token, sid: sid};
    }

    function clearCredentials() {
      sessionStorage.removeItem('sid');
      sessionStorage.removeItem('auth_token');
      sessionStorage.removeItem('username');
      //$sessionStorage.rvdCredentials = null;
    }

    // public interface
    return {
        getCredentials: getCredentials,
        setCredentials: setCredentials,
        clearCredentials: clearCredentials
    }

});

angular.module('Rvd').service('initializer',function (authentication, storage,  $q) {
    return {
        init: function () {
            var initPromise = $q.defer();
            // Put initialization operations here. Resolve initPromise when done.
            // ...
            initPromise.resolve();
            return initPromise.promise;
        }
    };
});

angular.module('Rvd').service('authentication', function ($http, $q, storage, $state, md5, $rootScope, RvdConfiguration, accountProfilesCache) {
  var authInfo = {};
	var account = null; // if this is set it means that user logged in: authentication succeeded and account was retrieved

  $rootScope.authInfo = authInfo;

	function getAccount() {
	    return account;
	}

	function setAccount(acc) {
	    account = acc;
	    if (account) {
	        authInfo.username = account.email_address;
	    } else {
	        //clear authInfo properties
	        delete authInfo.username;
	    }
	}

	function getAuthInfo() {
	    return authInfo;
	}

	function getUsername() {
	    if (account)
	        return account.email_address;
	    return null;
	}

	function getAuthHeader() {
	    if (account)
	        return "Basic " + btoa(account.sid + ":" + account.auth_token);
	     return null;
	}

	// parses the link header from 'GET Account' and returns a url to the profile or 'undefined'
	function parseProfileLink(linkHeader) {
	  if (linkHeader) {
	    var m = linkHeader.match("<(.*)>");
	    if (m) {
	      return m[1];
	    }
	  }
	}

    /*
	  Returns a promise
	    resolved: nothing is really returned. The following assumptions stand:
	        - getAccount() will have a valid authenticated account
	        - using the credentials of the account one can access both Restcomm and RVD.
	    rejected:
	        - RVD_ACCESS_OUT_OF_SYNC. Restcomm authentication succeeded but RVD failed. RVD is not operational. account and storage credentnials will be cleared.
	        - NEED_LOGIN. Authentication failed. User will have to try the login screen (applies for restcomm auth type)
	*/
	function restcommLogin(usernameOrSid,password,token) {
	    var deferredLogin = $q.defer();
	    var secret = !!password ? md5.createHash(password) : token; // use 'password' as secret value if set. Otherwise use 'token'
	    var authHeader = basicAuthHeader(usernameOrSid, secret);
        $http({method:'GET', url: RvdConfiguration.restcommBaseUrl + '/restcomm/2012-04-24/Accounts.json/' + encodeURIComponent(usernameOrSid), headers: {Authorization: authHeader}}).then(function (response) {
            var acc = response.data; // store temporarily the account returned
            var link = parseProfileLink( response.headers('link') ); // store temporarily until we do login
            $http({method:'GET', url:'services/auth/keepalive', headers: {Authorization: "Basic " + btoa(acc.sid + ":" +acc.auth_token)}}).then(function (response) {
                // ok, access to both restcomm and RVD is verified
                setAccount(acc);
                authInfo.username = acc.email_address; // TODO will probably add other fields here too that are not necessarily tied with the Restcomm account notion
                storage.setCredentials(null,acc.auth_token,acc.sid);
                $rootScope.$broadcast('logged-in', {accountId: acc.sid, profileLink: link});
                deferredLogin.resolve();
            }, function (response) {
                setAccount(null);
                storage.clearCredentials();
                $rootScope.$broadcast('logged-out');
                deferredLogin.reject('RVD_ACCESS_OUT_OF_SYNC');
            });
        }, function (response) {
            // restcomm authentication failed with stored credentials
            storage.clearCredentials();
            deferredLogin.reject('NEED_LOGIN');
        });
        return deferredLogin.promise;
	}

	// checks that typical access to RVD services is allowed. A required role can be passed too
	/*
	  Returns
	    on success; nothing is really returned. Implies the following:
	        - storage.getCredentials() hold a valid set of {username,password,sid} values.
	        - check restcommLogin() for additional assumptions
	    throws:
	        - NEED_LOGIN. Authentication failed. User will have to try the login screen (applies for restcomm auth type)
	        - UNSUPPORTED_AUTH_TYPE. Restcomm authentication is disabled but alternative (keycloak) is not yet supported.
	        - chains other errors fro restcommLogin()
	*/
	function checkRvdAccess(role) {
        if (!account) {
            // There is no account set. If there are credentials in the storage we will try logging in using them
            var creds = storage.getCredentials();
            if (creds.sid) {
                return restcommLogin(creds.sid, null, creds.auth_token); // a chained promise is returned
            } else
                throw 'NEED_LOGIN';
        } else {
            return; // everythig is OK!
        }
	}

    // creates an auth header using a username (or sid) and a plaintext password (not already md5ed)
	function basicAuthHeader(username, secret) {
	    var auth_header = "Basic " + btoa(username + ":" + secret);
        return auth_header;
	}

    function doLogin(username, password) {
        return restcommLogin(username,password);
    }

    function doLogout() {
        storage.clearCredentials();
        setAccount(null);
        $rootScope.$broadcast('logged-out');
    }

    // public interface

    return {
        getAccount: getAccount,
        getUsername: getUsername,
        checkRvdAccess: checkRvdAccess,
        doLogin: doLogin,
        doLogout: doLogout,
        getAuthInfo: getAuthInfo,
        getAuthHeader: getAuthHeader
	}
});

angular.module('Rvd').service('projectSettingsService', ['$http','$q','$modal', '$resource', function ($http,$q,$modal,$resource) {
	//console.log("Creating projectSettigsService");
	var service = {};
	var cachedProjectSettings = {};

	// returns project settings from cache
	service.getProjectSettings = function () {
		return cachedProjectSettings;
	}

	// refreshes cachedProjectSettings asynchronously
	service.refresh = function (applicationSid) {
		var resource = $resource('services/projects/:applicationSid/settings');
		cachedProjectSettings = resource.get({applicationSid:applicationSid});
	}

/*
    // this seems not be used anywhere:
	service.retrieve = function (applicationSid) {
		var deferred = $q.defer();
		$http({method:'GET', url:'services/projects/'+applicationSid+'/settings'})
		.success(function (data,status) {
		    if (data === null) {
		        cachedProjectSettings = {logging:false};
                deferred.resolve(cachedProjectSettings);
		    } else {
                cachedProjectSettings = data;
                deferred.resolve(cachedProjectSettings);
			}
		})
		.error(function (data,status) {
		    deferred.reject("ERROR_RETRIEVING_PROJECT_SETTINGS");
		});
		return deferred.promise;
	}
	*/

	service.save = function (applicationSid, projectSettings) {
		var deferred = $q.defer();
		$http({method:'POST',url:'services/projects/'+applicationSid+'/settings',data:projectSettings})
		.success(function (data,status) {deferred.resolve()})
		.error(function (data,status) {deferred.reject('ERROR_SAVING_PROJECT_SETTINGS')});
		return deferred.promise;
	}

	function projectSettingsModelCtrl ($scope, projectSettings, projectSettingsService, applicationSid, projectName, $modalInstance, notifications) {
		//console.log("in projectSettingsModelCtrl");
		$scope.projectSettings = projectSettings;
		$scope.projectName = projectName;
		$scope.applicationSid = applicationSid;

		$scope.save = function (applicationSid, data) {
			//console.log("saving projectSettings for " + name);
			service.save(applicationSid, data).then(
				function () {$modalInstance.close()},
				function () {notifications.put("Error saving project settings")}
			);
		}
		$scope.cancel = function () {
			$modalInstance.close();
		}

		$scope.changeLoggingSetting = function () {
			if($scope.projectSettings.logging == false && $scope.projectSettings.loggingRCML == true){
				$scope.projectSettings.loggingRCML = false;
			}
		}
	}

	service.showModal = function(applicationSid, projectName) {
		var modalInstance = $modal.open({
			  templateUrl: 'templates/projectSettingsModal.html',
			  controller: projectSettingsModelCtrl,
			  size: 'lg',
			  resolve: {
				projectSettings: function () {
					var deferred = $q.defer()
					$http.get("services/projects/"+applicationSid+"/settings")
					.then(function (response) {
						deferred.resolve(response.data);
					}, function (response) {
						if ( response.status == 404 )
							deferred.resolve({});
						else {
							deferred.reject();
						}
					});
					return deferred.promise;
				},
				projectName: function () {return projectName;},
				applicationSid: function () {return applicationSid;}
			  }
			});

			modalInstance.result.then(function (projectSettings) {
				service.refresh(applicationSid);
				//console.log(projectSettings);
			}, function () {});
	}

	return service;
}]);


angular.module('Rvd').service('webTriggerService', ['$http','$q','$modal', function ($http,$q,$modal) {
	//console.log("Creating webTriggerService");
	var service = {};
	service.retrieve = function (applicationSid) {
		var deferred = $q.defer();
		$http({method:'GET', url:'services/projects/'+applicationSid+'/cc'})
		.success(function (data,status) {deferred.resolve(data)})
		.error(function (data,status) {
			if (status == 404)
				deferred.resolve({logging:false});
			else
				deferred.reject("ERROR_RETRIEVING_PROJECT_CC");
		});
		return deferred.promise;
	}

	service.save = function (applicationSid, ccInfo) {
		var deferred = $q.defer();
		$http({method:'POST',url:'services/projects/'+applicationSid+'/cc',data:ccInfo})
		.success(function (data,status) {deferred.resolve()})
		.error(function (data,status) {deferred.reject('ERROR_SAVING_PROJECT_CC')});
		return deferred.promise;
	}

	function webTriggerModalCtrl ($scope, ccInfo, applicationSid, rvdSettings, $modalInstance, notifications, $location) {
		$scope.save = function (applicationSid, data) {
			//console.log("saving ccInfo for " + name);
			service.save(applicationSid, data).then(
				function () {$modalInstance.close()},
				function () {notifications.put("Error saving project ccInfo")}
			);
		}
		$scope.cancel = function () {
			$modalInstance.close();
		}
		$scope.disableWebTrigger = function () { $scope.ccInfo = null; }
		$scope.enableWebTrigger = function () {
			if ($scope.ccInfo == null)
				$scope.ccInfo = createCcInfo();
		}
		$scope.getWebTriggerUrl = function () {
			return $location.protocol() + "://" + $location.host() + ":" +  $location.port() + "/visual-designer/services/apps/" +  applicationSid + '/start<span class="text-muted">?from=12345&amp;to=+1231231231&amp;token=mysecret</span>';
		};
		$scope.getRvdHost = function() {
			return $location.host();
		}
		$scope.getRvdPort = function() {
			return $location.port();
		}

		function createCcInfo() {
			return {lanes:[{startPoint:{to:"",from:""}}]};
		}
		function setWebTriggerStatus(webTriggerEnabled) {
			if ( webTriggerEnabled )
				$scope.enableWebTrigger();
			else
				$scope.disableWebTrigger();
		}
		$scope.setWebTriggerStatus = setWebTriggerStatus;

		if (ccInfo == null) {
			ccInfo = {};
			$scope.webTriggerEnabled = false;
		} else
			$scope.webTriggerEnabled = true;
		$scope.ccInfo = ccInfo;
		setWebTriggerStatus($scope.webTriggerEnabled);

		$scope.applicationSid = applicationSid;
		$scope.rvdSettings = rvdSettings;
	}

	service.showModal = function(applicationSid) {
		var modalInstance = $modal.open({
			  templateUrl: 'templates/webTriggerModal.html',
			  controller: webTriggerModalCtrl,
			  size: 'lg',
			  resolve: {
				ccInfo: function () {
					var deferred = $q.defer()
					$http.get("services/projects/"+applicationSid+"/cc")
					.then(function (response) {
						deferred.resolve(response.data);
					}, function (response) {
						if ( response.status == 404 )
							deferred.resolve(null);
						else {
							deferred.reject();
						}
					});
					return deferred.promise;
				},
				applicationSid: function () {return applicationSid;},
				rvdSettings: function (rvdSettings) {
					return rvdSettings.refresh();
				}
			  }
			});

			modalInstance.result.then(function (ccInfo) {
			}, function () {});
	}

	return service;
}]);

angular.module('Rvd').factory('parametersResource', function ($resource) {
  return $resource('api/projects/:applicationSid/parameters', {}, {
    update: {
      method: "PUT"
    }
  });
});

angular.module('Rvd').service('parametersService', function ($q,$modal,parametersResource) {
	//console.log("Creating webTriggerService");
	var service = {};

	function parametersModalCtrl ($scope, applicationSid, parameters, $modalInstance, notifications, parametersResource) {
	  $scope.applicationSid = applicationSid;
	  $scope.parametersResponse = parameters;

		$scope.save = function (applicationSid ) {
		  parametersResource.update({applicationSid: applicationSid}, $scope.parametersResponse, function () {
		    $modalInstance.close();
		  }, function () {
		    notifications.put("Error saving project parameters")
		  });
		}
		$scope.cancel = function () {
			$modalInstance.close();
		}
	}

	service.showModal = function(applicationSid) {
		var modalInstance = $modal.open({
			  templateUrl: 'templates/parametersModal.html',
			  controller: parametersModalCtrl,
			  size: 'lg',
			  resolve: {
          parameters: function (parametersResource) {
            return parametersResource.get({applicationSid: applicationSid});
          },
          applicationSid: function () {
            return applicationSid;
          }
			  }
			});

			modalInstance.result.then(function (ccInfo) {
			}, function () {});
	}

	return service;
});


angular.module('Rvd').service('projectLogService', ['$http','$q','$stateParams', 'notifications', function ($http,$q,$stateParams,notifications) {
	var service = {};
	service.retrieve = function () {
		var deferred = $q.defer();
		$http({
		    method:'GET',
		    url:'services/apps/'+$stateParams.applicationSid+'/log',
		    transformResponse: [function (data) {
                  // avoid using default JSON parser for response
                  return data;
            }]
		})
		.success(function (data,status) {
			console.log('retrieved log data');
			deferred.resolve(data);
		})
		.error(function (data,status) {
			deferred.reject();
		});
		return deferred.promise;
	}
	service.reset = function () {
		var deferred = $q.defer();
		$http({method:'DELETE', url:'services/apps/'+$stateParams.applicationSid+'/log'})
		.success(function (data,status) {
			console.log('reset log data');
			notifications.put({type:'success',message:$stateParams.projectName+' log reset'});
			deferred.resolve();
		})
		.error(function (data,status) {
			//notifications.put({type:'danger',message:'Cannot reset '+$stateParams.projectName+' log'});
			deferred.reject();
		});
		return deferred.promise;
	}

	return service;
}]);

angular.module('Rvd').service('rvdSettings', ['$http', '$q', function ($http, $q) {
	var service = {data:{}};
	var defaultSettings = {appStoreDomain:"apps.restcomm.com"};
	var effectiveSettings = {};

	function updateEffectiveSettings (retrievedSettings) {
		angular.copy(defaultSettings,effectiveSettings);
		angular.extend(effectiveSettings,retrievedSettings);
	}

	service.saveSettings = function (settings) {
		var deferred = $q.defer();
		$http.post("services/settings", settings, {headers: {'Content-Type': 'application/data'}}).success( function () {
			service.data = settings; // since this is a successfull save, update the internal settings data structure
			updateEffectiveSettings(settings);
			deferred.resolve();
		}).error(function () {
			deferred.reject();
		});
		return deferred.promise;
	}

	/* retrieves the settings from the server and updates stores them in an internal service object */
	service.refresh = function () {
		var deferred = $q.defer();
		$http.get("services/settings")
		.then(function (response) {
			service.data = response.data;
			updateEffectiveSettings(service.data);
			deferred.resolve(service.data);
		}, function (response) {
			if ( response.status == 404 ) {
				angular.copy(defaultSettings,effectiveSettings);
				service.data = {};
				deferred.resolve(service.data);
			}
			else {
				deferred.reject();
			}
		});
		return deferred.promise;
	}

	service.getEffectiveSettings = function () {
		return effectiveSettings;
	}
	service.getDefaultSettings = function () {
		return defaultSettings;
	}

	return service;
}]);

angular.module('Rvd').service('variableRegistry', [function () {
	var service = {
		lastVariableId: 0,
		variables: []
	};

	service.newId = function () {
		service.lastVariableId ++;
		return service.lastVariableId;
	}

	service.addVariable = function (varInfo) {
		//console.log('adding variable' + varInfo.id)
		service.variables.push(varInfo);
	}

	service.removeVariable = function (varInfo) {
		//console.log('removing variable' + varInfo.id);
		service.variables.splice(service.variables.indexOf(varInfo), 1);
	}

	function registerVariable(name) {
		var newid = service.newId();
		service.addVariable({id:newid, name:name});
	}

	service.listUserDefined = function () {
		return variables;
	}
	service.listAll = function () {
		return service.variables;
	}

	registerVariable("core_To");
	registerVariable("core_From");
	registerVariable("core_CallSid");
	registerVariable("core_AccountSid");
	registerVariable("core_CallStatus");
	registerVariable("core_ApiVersion");
	registerVariable("core_Direction");
	registerVariable("core_CallerName");
    registerVariable("core_CallTimestamp");
    registerVariable("core_ForwardedFrom");
    registerVariable("core_InstanceId");
    registerVariable("core_ReferTarget");
    registerVariable("core_Transferor");
    registerVariable("core_Transferee");
	// after collect, record, ussdcollect
	registerVariable("core_Digits");
	// after dial
	registerVariable("core_DialCallStatus");
	registerVariable("core_DialCallSid");
	registerVariable("core_DialCallDuration");
	registerVariable("core_DialRingDuration");
	registerVariable("core_RecordingUrl");
	// after record
	//registerVariable("core_RecordingUrl");
	registerVariable("core_RecordingDuration");
	// after dial or record
	registerVariable("core_PublicRecordingUrl");
	// after sms
	registerVariable("core_SmsSid");
	registerVariable("core_SmsStatus");
	// after fax
	registerVariable("core_FaxSid");
	registerVariable("core_FaxStatus");
	// SMS project
	registerVariable("core_Body");


	return service;
}]);

// An simple inter-service communications service
angular.module('Rvd').service('communications', [function () {
	// for each type of event create a new set of *Handlers array, a subscribe and a publish function
	var newNodeHandlers = [];
	var nodeRemovedHandlers = [];
	return {
		subscribeNewNodeEvent: function (handler) {
			newNodeHandlers.push(handler);
		},
		publishNewNodeEvent: function (data) {
			angular.forEach(newNodeHandlers, function (handler) {
				handler(data);
			});
		},
		subscribeNodeRemovedEvent: function (handler) {
			nodeRemovedHandlers.push(handler);
		},
		publishNodeRemovedEvent: function (data) {
			angular.forEach(nodeRemovedHandlers, function (handler) {
				handler(data);
			});
		}
	}
}]);

angular.module('Rvd').service('editedNodes', ['communications', function (communications) {
	var service = {
		nodes: [],
		activeNodeIndex : -1 // no node is active (visible)
	}

	// makes a node edited. The node should already exist in the registry (this is not verified)
	function addEditedNode(nodeName) {
		// maybe check if the node exists
		if ( getNodeIndex(nodeName) == -1 ) {
			service.nodes.push({name:nodeName});
		}
	}

	// a new node has been added to the registry
	function onNewNodeHandler(nodeName) {
		console.log("editedNodes: new node created: " + nodeName );
		addEditedNode(nodeName);
	}

	// Finds the node's index by name. Returns -1 if not found
	function getNodeIndex(nodeName) {
		for (var i=0; i<service.nodes.length; i++) {
			if ( service.nodes[i].name == nodeName ) {
				return i;
				break;
			}
		}
		return -1;
	}

	function setActiveNode(nodeName) {
		service.activeNodeIndex = getNodeIndex(nodeName);
	}

	function isNodeActive(nodeName) {
		var i = getNodeIndex(nodeName);
		if ( i != -1 && i == service.activeNodeIndex )
			return true;
		return false;
	}

	function getActiveNode(nodeName) {
		if ( service.activeNodeIndex != -1 )
			return service.nodes[service.activeNodeIndex].name;
		// else return undefined
	}


	// triggered when a node will be removed form the registry. We remove this editedNode and update actieNodeIndex accordingly
	function onNodeRemovedHandler(nodeName) {
		// if this is the active node, activate the next one
		var i = getNodeIndex(nodeName);

		if ( i != -1 ) {
			service.nodes.splice(i,1);

			if ( i < service.activeNodeIndex )
				service.activeNodeIndex --;
			if (i >= service.nodes.length)
				service.activeNodeIndex = service.nodes.length - 1; // this even works whan the last node is removed
		} else
			console.log("Error removing module " + nodeName +". It does not exist");

	}

	function getEditedNodes() {
		return service.nodes;
	}

	function clear() {
		service.nodes = [];
		service.activeNodeIndex = -1;
	}


	// event handlers
	communications.subscribeNewNodeEvent(onNewNodeHandler);
	communications.subscribeNodeRemovedEvent(onNodeRemovedHandler);

	// public interface
	service.setActiveNode = setActiveNode;
	service.getActiveNode = getActiveNode;
	service.addEditedNode = addEditedNode;
	service.getEditedNodes = getEditedNodes;
	service.isNodeActive = isNodeActive;
	service.clear = clear;
	service.removeEditedNode = onNodeRemovedHandler;

	return service;
}]);

angular.module('Rvd').service('nodeRegistry', ['communications', function (communications) {

	var service = {
		lastNodeId: 0,
		nodes: [],
		nodesByName : {}
	};

	function newName() {
		var id = ++service.lastNodeId;
		return "module" + id;
	}

	// Pushes a new node in the registry If it doesn't have an id it assigns one to it
	function addNode(node) {
		if (node.name) {
			// Node already has an id. Update lastNodeId if required
			//if (lastNodeId < node.id)
			//	lastNodeId = node.id;
			// it is dangerous to add a node with an id less that lastNodeId
			// else ...
		} else {
			var name = newName();
			node.setName(name);
		}
		service.nodes.push(node);
		service.nodesByName[node.name] = node;

		//communications.publishNewNodeEvent(node.name);
	}
	function removeNode(nodeName) {
		var node = getNode(nodeName);
		if ( node ) {
			communications.publishNodeRemovedEvent(node.name);
			service.nodes.splice(service.nodes.indexOf(node), 1);
			delete service.nodesByName[node.name];
		} else
			console.log("Cannot remove node " + nodeName + ". Node does not exist");
	}
	function getNode(name) {
		return service.nodesByName[name];
		//for (var i=0; i < service.nodes.length; i ++) {
		//	if ( service.nodes[i].name == name )
		//		return service.nodes[i];
		//}
	}
	// dangerous! nodesByName object can be changed and trash service structure
	//function getNodesByName() {
	//	return service.nodesByName;
	//}
	function getNodes() {
		return service.nodes;
	}
	function reset(lastId) {
		if ( ! lastId )
			service.lastNodeId = 0;
		else
			service.lastNodeId = lastId;
	}
	function clear() {
		service.lastNodeId = 0;
		service.nodes = [];
		service.nodesByName = {};
	}

	// public interface
	service.addNode = addNode;
	service.removeNode = removeNode;
	service.getNode = getNode;
	service.getNodes = getNodes;
	service.reset = reset;
	service.clear = clear;

	return service;
}]);

angular.module('Rvd').factory('stepService', [function() {
	var stepService = {
		serviceName: 'stepService',
		lastStepId: 0,

		newStepName: function () {
			return 'step' + (++this.lastStepId);
		}
	};

	return stepService;
}]);

/* Service that pings RVD to keep the ticket fresh */
angular.module('Rvd').factory('keepAliveResource', function($resource) {
    return $resource('services/auth/keepalive');
});



angular.module('Rvd').factory('fileRetriever', function (Blob, FileSaver, $http) {
    // Returns a promise.
    // resolved: nothing is returned - the file has been saved normally
    // rejected: ERROR_RETRIEVING_FILE - either an HTTP, or empty file returned
    function download(downloadUrl, filename, contentType) {
        contentType = contentType || 'application/zip'; // contentType defaults to application/zip
        // returns a promise
	    return $http({
	        method: 'GET',
	        url: downloadUrl,
            headers: { accept: contentType },
	        responseType: 'arraybuffer',
            cache: false,
            transformResponse: function(data, headers) {
                var zip = null;
                if (data) {
                    zip = new Blob([data], {
                        type: contentType
                    });
                }
                var result = {blob: zip};
                return result;
            }
	    }).then(function (response) {
            if (response.data.blob) {
                FileSaver.saveAs(response.data.blob, filename);
                return;
            } else
                throw 'ERROR_RETRIEVING_FILE';
	    }, function () {
	        throw 'ERROR_RETRIEVING_FILE';
	    });
	}

	return {
	    download: download
	}
});


// Temporarily stores current url (part that follows #) so that user can later be navigated back to it after login. It is used when a user gets an authentication error while accessing some part of the UI.
angular.module('Rvd').factory('urlStateTracker', function () {
    var oldUrl;
    urlStateTracker = {};

    // stores the current url (which it takes from angular $location service. Note. $location is NOT INJECTED below)
    urlStateTracker.remember = function ($location) {
        //console.log("remembering state " + $location.url() );
        oldUrl = $location.url();
    }

    urlStateTracker.recall = function () {
        if (!!oldUrl) {
            var ret = oldUrl;
            oldUrl = "";
            return ret;
        }
        else
            return "";
    }

    return urlStateTracker;
});

angular.module('Rvd').factory('versionChecker', function () {
    return {
        status: function(rvdVersion, projectVersion) {
            if (!!projectVersion) {
                if (projectVersion == rvdVersion )
                    return "CURRENT";
                var r = new RegExp("^([0-9]+)\\.([0-9]+)");
                var matchesRvd = r.exec(rvdVersion);
                var matchesProject = r.exec(projectVersion);
                //  match[1] is major version, match[2] is minor version
                if (!!matchesProject && matchesProject.length >= 2) {
                    // projectVersion seems properly formated. Let's check major/minor
                    if (matchesProject[1] == matchesRvd[1] && matchesProject[2] > matchesRvd[2])
                        return "FUTURE"; // this looks like a future project maybe created from a newer binary
                    if (matchesProject[1] == matchesRvd[1] && matchesProject[2] < matchesRvd[2])
                        return "OLD"; // this looks like an old project
                    if (matchesProject[1] == matchesRvd[1] && matchesProject[2] == matchesRvd[2])
                        return "CURRENT"; // we don't check for second minorer version
                    if (matchesProject[1] != matchesRvd[1])
                        return "INCOMPATIBLE"; // major versions differ. This project is probably broken
                }
            }
            return "UNKNOWN";

        }
    }
});

angular.module('Rvd').factory('applicationsResource', function ($resource) {
  return $resource('/restcomm/2012-04-24/Accounts/:accountId/Applications/:applicationId.json');
});


// initialize with link to profile before using. It will also convert absolute links to relative
angular.module('Rvd').factory('accountProfilesCache', function ($resource) {
  var profileLink;
  var profilesResource;
  var cache;

  return {
    setProfileLink: function (link) {
      if (link) {
        if (link.startsWith('http:') || link.startsWith('https:')) {
          // this is an absolute link
          var l = new URL(link);
          profileLink = l.pathname;
        } else {
          profileLink = link;
        }
      }
    },
    refresh: function () {
      cache = undefined;
      profilesResource = undefined;
      if (!profileLink) {
        console.error("No profileLink available. I can't fetch profile.");
      } else {
        profilesResource = $resource(profileLink);
        cache = profilesResource.get();
        return cache;
      }
    },
    get: function() {
      return cache;
    },
    clear: function () {
      profileLink = undefined;
      profilesResource = undefined;
      cache = undefined;
    }
  }
});

angular.module('Rvd').factory('featureAccessControl', function () {
  return {
    validate: function (number, profile, validator) {
      if (number) {
        if (validator == 'outboundPSTN') {
          if (!profile.featureEnablement.outboundPSTN)
            return { status: 'blocked', message: "calls to PSTN numbers are disabled" };
          var allowedPrefixes = profile.featureEnablement.outboundPSTN.allowedPrefixes;
          var blockedPrefixes = profile.featureEnablement.outboundPSTN.blockedPrefixes;
          if (allowedPrefixes) {
            for ( var i=0; i< allowedPrefixes.length; i++) {
              if ( number.startsWith(allowedPrefixes[i]) )
                return;
            }
          }
          if (blockedPrefixes) {
            for (var  i =0; i<blockedPrefixes.length; i++) {
              if ( number.startsWith(blockedPrefixes[i]) )
                return { status: 'blocked', message: "calling this number is not allowed" };
            }
          }
          // if it hasn't been blocked so far, assume allowed
          return;
        } else
        if (validator == 'outboundSMS') {
          if (!profile.featureEnablement.outboundSMS)
            return { status: 'maybe-blocked', message: "sending SMS to PSTN numbers is not available" };
          var allowedPrefixes = profile.featureEnablement.outboundSMS.allowedPrefixes;
          var blockedPrefixes = profile.featureEnablement.outboundSMS.blockedPrefixes;
          if (allowedPrefixes) {
            for ( var i=0; i< allowedPrefixes.length; i++) {
              if ( number.startsWith(allowedPrefixes[i]) )
                return;
            }
          }
          if (blockedPrefixes) {
            for (var  i =0; i<blockedPrefixes.length; i++) {
              if ( number.startsWith(blockedPrefixes[i]) )
                return { status: 'blocked', message: "sending SMS to this number is not allowed" };
            }
          }
          // if it hasn't been blocked so far, assume allowed
          return;
        }
      }
    }
  }
});
