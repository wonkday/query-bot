var app = angular.module('app', ['ngTouch', 'ui.grid', 'ui.grid.cellNav', 'ui.grid.edit', 'ui.grid.resizeColumns', 'ui.grid.pinning', 'ui.grid.selection', 'ui.grid.moveColumns', 'ui.grid.exporter', 'ui.grid.importer', 'ui.grid.grouping']);
 
app.controller('MainCtrl',  ['$scope', '$http', '$timeout', '$interval', 'uiGridConstants',
 function ($scope, $http, $timeout, $interval, uiGridConstants) {
 
  $scope.gridOptions = {};
  $scope.gridOptions.data = 'myData';
  $scope.gridOptions.enableColumnResizing = true;
  $scope.gridOptions.enableFiltering = true;
  $scope.gridOptions.enableGridMenu = true;
  $scope.gridOptions.showGridFooter = true;
  $scope.gridOptions.showColumnFooter = true;
  $scope.gridOptions.fastWatch = true;
 
  $scope.gridOptions.rowIdentity = function(row) {
    return row.id;
  };
  $scope.gridOptions.getRowIdentity = function(row) {
    return row.id;
  };
 
  $scope.gridOptions.columnDefs = [
    { name:'id', width:50 },
    { name:'name', width:100 },
    { name:'age', width:100, enableCellEdit: true, aggregationType:uiGridConstants.aggregationTypes.avg,
     cellTemplate: '<div class="ui-grid-cell-contents"><span>Age:{{COL_FIELD}}</span></div>'   },
    { name:'address.street', width:150, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Street:{{COL_FIELD}}</span></div>'   },
    { name:'address.city', width:150, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>City:{{COL_FIELD}}</span></div>'  },
    { name:'address.state', width:50, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>State:{{COL_FIELD}}</span></div>'  },
    { name:'address.zip', width:50, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Zip:{{COL_FIELD}}</span></div>'  },
    { name:'company', width:100, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Company:{{COL_FIELD}}</span></div>'  },
    { name:'email', width:100, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Email:{{COL_FIELD}}</span></div>'  },
    { name:'phone', width:200, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Phone:{{COL_FIELD}}</span></div>'  },
    { name:'about', width:300, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>AAbout:{{COL_FIELD}}</span></div>'  },
    { name:'friends[0].name', displayName:'1st friend', width:150, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Friend0:{{COL_FIELD}}</span></div>'  },
    { name:'friends[1].name', displayName:'2nd friend', width:150, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Friend1:{{COL_FIELD}}</span></div>'  },
    { name:'friends[2].name', displayName:'3rd friend', width:150, enableCellEdit: true, cellTemplate: '<div class="ui-grid-cell-contents"><span>Friend2:{{COL_FIELD}}</span></div>'  },
    { name:'agetemplate',field:'age', width:150, cellTemplate: '<div class="ui-grid-cell-contents"><span>Age 2:{{COL_FIELD}}</span></div>' }
  ];
 
  $scope.callsPending = 0;
 
  var i = 0;
  $scope.refreshData = function(){
    $scope.myData = [];
 
    var start = new Date();
    var sec = $interval(function () {
      $scope.callsPending++;
      
      $http.get('/data/500_complex.json')
        .success(function(data) {
          $scope.callsPending--;
 
          data.forEach(function(row){
            row.name = row.name + ' iter ' + i;
            row.id = i;
            i++;
            $scope.myData.push(row);
          });
        })
        .error(function() {
          $scope.callsPending--
        });
    }, 200);
 
 
    $timeout(function() {
       $interval.cancel(sec);
       $scope.left = '';
    }, 2000);
 
  };
 
}]);