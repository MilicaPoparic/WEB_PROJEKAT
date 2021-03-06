Vue.component("organization", {
	data: function () {
		    return {
		      organizations: null,
		      role: ''
		    }
	},
	template: ` 
<div>
	<h3><b>Organizations</b></h3>:
	<table class="table" border="1">
	<tr bgcolor="#f2f2f2">
		<th>Name</th>
		<th>Caption</th>
		<th>Logo</th>
	</tr>
		
	<tr v-for="o in organizations">
		<td><a href="#" v-on:click="showDetails(o)">{{o.name}}</a></td>
		<td>{{o.caption}}</td>
		<td><img v-bind:src="o.logo" alt="Logo" height=35 width=60 ></td>	
		
	</tr>
</table>
		<br>
		<p><button v-if="role=='superAdmin'" v-on:click="addOrg">Add</button></p>
	
</div>		  
`,
	methods : {
		addOrg : function() {
			location.href = '#/addOrg'
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
				    		  //location.href = '#/c';
				    		  axios
				  	        .get('rest/checkSuperAdminAdmin')
				  	        .then((response) => {
				  	        	if(response.status == 200) {
				  	        		location.href = '#/o';
				  				    }
				  				   })
				  				   .catch((response)=>{
				  				    	location.href = '#/forbidden';
				  				      })
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