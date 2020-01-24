Vue.component("change-user",{
	data: function(){
		return{
			user: {},
			err: '',
		    logoErr: ''
		}
	},
	template:
		`
<div>
		<p>User data</p>
		<table>
		<tr>
				<td> Email: </td>
				<td>{{user.email}}</td> 
		</tr>
		<tr >	
				<td> Organization: </td>
				<td>{{user.nameORG}}</td>
		</tr>		
		<tr >
				<td>Name: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.name" name="name" ></td> 	
		</tr>
		<tr >
				<td>Surname: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.surname" name="surname" ></td> 	
		</tr>
		<tr >
				<td>Password: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.password" name="password" ></td> 	
		</tr>
		<tr >
			<td>Role: </td>
			<td><select v-model="user.role">
				<option >admin</option>
				<option >user</option>
			</select></td>
		</tr>
		<tr>
			<td><button v-on:click="change()">Change data</button></td> 
			<td><button v-on:click="deleteUser()">Delete</button></td> 
		</tr>
		<tr>{{err}}</tr>
		</table>
		

</div>
			`
,
	methods : {
		change : function() {
			if(!this.user.name){
				this.nameErr = 'Name is required!'
			}
			if(!this.user.surname){
				this.captionErr = 'Surname is required!'
			}
			if (this.user.name && this.user.surname && this.user.password) {
				axios
				.post('rest/changeUser', this.user)
				.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/users';
		    	  }
		      })
				.catch(response=>this.err = 'GRESKA SA SERVERA!')
			}
		},
		deleteUser : function() {
		axios
		.post('rest/deleteUser', this.user)
		.then(response => location.href = '#/users')
		.catch(response=>this.err = 'ERROR 400!')
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
			  	        		location.href = '#/changeUser';
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
          .get('rest/getUser')
          .then(response => (this.user = response.data))
        
    },
});