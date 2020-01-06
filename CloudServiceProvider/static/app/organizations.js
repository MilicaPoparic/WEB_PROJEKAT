Vue.component("organization", {
	data: function () {
		    return {
		      organizations: null,
		      role: ''
		    }
	},
	template: ` 
<div>
	Organizations:
	<table border="1">
	<tr bgcolor="lightgrey">
		<th>Name</th>
		<th>Caption</th>
		<th>Logo</th>
		<th>&nbsp;</th>
	</tr>
		
	<tr v-for="o in organizations">
		<td>><a href="#" v-on:click="showDetails(o)">{{o.name}}</a></td>
		<td>{{o.caption}}</td>
		<td><img src="{{o.logo}}" alt="Logo" height=5 width=5></td>	
	</tr>
</table>
		<p><button v-if="role=='superAdmin'" v-on:click="addOrg">Add</button></p>
	
</div>		  
`,
	methods : {
		addOrg : function() {
			axios
		      .post('rest/requestAddOrg')
		      .then(response => location.href = '#/addOrg');
		},
		showDetails : function(o) {
			axios
		      .post('rest/captureOrg', o)
		      .then(response => location.href = '#/changeOrg');
		}
		
	}
	,
	mounted () {
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/o';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
          .get('rest/getOrganizations')
          .then(response => (this.organizations = response.data))
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));

    },
});