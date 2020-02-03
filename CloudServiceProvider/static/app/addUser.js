Vue.component("add-user", {
	data: function () {
	    return {
	     user: {},
	     organizations: null,
	     organization: '',
	     err: '',
	     newRole: '',
	     role: '',
	     errEmail: '',
	     errName: '',
	     errSurname: '',
	     errPass: ''
	    }
},
	template: ` 
<div>
	<p>ADDING USER</p>
	<table>
		<tr><td>Email:</td>
		<td><input type="text"  v-model="user.email" name="email" required></td> {{errEmail}} </tr>
		
		<tr><td>Name:</td><td>
		<input type="text" v-model="user.name" name="name" required></td> {{errName}} </tr>
		
		<tr><td>Surname:</td>
		<td><input type="text" v-model="user.surname" name="surname" required></td> {{errSurname}}  </tr>
		
		<tr><td>Password:</td>
		<td><input type="text" v-model="user.password" name="password" required></td> {{errPass}} </tr>
		
		<tr v-if="role=='superAdmin'"><td>Organization:</td>
		<td><select v-model="user.organization">
		<option v-for="o in organizations">{{o.name}}</option>
		</select></td></tr>
		
		<tr><td>Role: </td>
		<td><select v-model="user.role">
		<option >admin</option>
		<option >user</option>
		</select></td></tr>
		<tr><button v-on:click="add">Add</button></tr>
	</table>
		{{err}}
</div>		  
`
		
	, 
	methods : {
		add : function() {
			this.errEmail=''; this.errName=''; this.errSurname=''; this.errPass='';  this.err='';
			if (this.organizations.length==1){
				this.user.organization = this.organizations[0].name; 
				console.log(this.user.organization);
			}
			if (this.user.email && this.user.name && this.user.surname && this.user.password && this.user.organization) {
				axios
				.post('rest/addUser', this.user)
				.then((response) => {
					if(response.status == 200) {
						location.href = '#/users';
					}
				})
				.catch(response=> this.errEmail='Email must be unique!')
				return;
			}
			if (!this.user.email) {
				this.errEmail = 'Email is required';
			}
			if (!this.user.name) {
				this.errName= 'Name is required!';
			}
			if (!this.user.surname) {
				this.errSurname = 'Surname is required!';
			}
			if (!this.user.password) {
				this.errPass = 'Password is required!';
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