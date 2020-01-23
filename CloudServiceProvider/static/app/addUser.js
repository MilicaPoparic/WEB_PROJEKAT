Vue.component("add-user", {
	data: function () {
	    return {
	     email: '',
	     name: '',
	     surname: '',
	     password: '',
	     organizations: null,
	     organization: '',
	     err: '',
	     newRole: '',
	     role: ''
	    }
},
	template: ` 
<div>
	<p>ADDING USER</p>
	<table>
		<tr><td>Email:</td><td><input type="text" style="width:60px" size="5" v-model="email" name="email" required></td></tr>
		<tr><td>Name:</td><td><input type="text" style="width:60px" size="5" v-model="name" name="name" required></td></tr>
		<tr><td>Surname:</td><td><input type="text" style="width:60px" size="5" v-model="surname" name="surname" required></td> </tr>
		<tr><td>Password:</td><td><input type="text" style="width:60px" size="5" v-model="password" name="password" required></td> </tr>
		<tr v-if="role=='superAdmin'"><td>Organization:</td><td><select v-model="organization">
		<option v-for="o in organizations">{{o.name}}</option>
		</select></td></tr>
		<tr><td>Role: </td><td><select v-model="newRole">
		<option >admin</option>
		<option >user</option>
		</select></td></tr>
		<tr><button v-on:click="add">Add</button></tr>
		<tr>{{err}}</tr>
	</table>
</div>		  
`
		
	, 
	methods : {
		add : function() {
		
			if (this.email && this.name && this.surname && this.password) {
				axios
				.post('rest/addUser', {"email":this.email, "name":this.name, "surname":this.surname, "nameORG":this.organization, "role": this.newRole, "password":this.password})
				.then((response) => {
					if(response.status == 200) {
						location.href = '#/users';
					}
				})
				.catch(response=> this.err='Email must be unique!')
				}
				else {
					this.err='Incomplete data!'
				}
		}

	},
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
			  	        		location.href = '#/addUser';
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
        .get('rest/getRole')
        .then(response => (this.role = response.data));
        axios
        .get('rest/getOrganizations')
        .then(response => (this.organizations = response.data))
        
    },


});