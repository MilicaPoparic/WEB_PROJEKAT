Vue.component("users", {
	data: function () {
		    return {
		      users: null,
		      role: ''
		    }
	},
	template: ` 
<div>
	Users:
	<table border="1">
	<tr bgcolor="lightgrey">
		<th>Email</th>
		<th>Name</th>
		<th>Surname</th>
		<th v-if="role=='superAdmin'">Organization</th>
	</tr>
		
	<tr v-for="u in users">
		<td><a href="#" v-on:click="showDetails(u)">{{u.email}}</a></td>
		<td>{{u.name}}</td>
		<td>{{u.surname}}</td>
		<td v-if="role=='superAdmin'">{{u.nameORG}}</td>	
	</tr>
</table>
		<p><button v-on:click="addUser">Add</button></p>
	
</div>		  
`,
	methods : {
		addUser : function() {
			location.href = '#/addUser';
		},
		showDetails : function(u) {
			axios
		      .post('rest/captureUser', u)
		      .then(response => location.href = '#/changeUser');
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
			  	        		location.href = '#/users';
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
          .get('rest/getUsers')
          .then(response => (this.users = response.data))
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));

    },
});