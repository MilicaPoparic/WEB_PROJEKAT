Vue.component("profile-info",{
	data: function(){
		return{
			user: {},
			confirmation: '',
			passErr: '',
			err: ''
		}
	},
	template:
		`
<div>
		<p>User data</p>
		<table>
		<tr >
				<td>Name: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.name" name="name" ></td> 	
		</tr>
		<tr >
				<td>Surname: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.surname" name="surname" ></td> 	
		</tr>
		<tr>
				<td> Email: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.email" name="email" ></td> 	
		</tr>
		<tr >
				<td>Password: </td>
				<td><input type="text" style="width:60px" size="3" v-model="user.password" name="password"></td> 	
		</tr>
		<tr >
				<td>Password: </td>
				<td><input type="text" style="width:60px" size="3" v-model="confirmation" name="password" > {{passErr}} </td> 	
		</tr>
		<tr >
			<td>Role: </td>
			<td>{{user.role}}</td>
		</tr>
		<tr>
			<td><button v-on:click="change()">Change data</button></td> 
		</tr>
		<tr>{{err}}</tr>
		</table>
		

</div>
			`
,
	methods : {
		change : function() {
			if (this.user.password === this.confirmation) {
				axios
				.post('rest/changeProfile', this.user)
				.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/h';
		    	  }
		      })
				.catch(response=>this.err = 'GRESKA SA SERVERA!')
				
			}
			else {
				this.passErr = "Please confirm password!";
			}
		}
	},
	mounted () {	
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/profile';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
          .get('rest/getLoggedInUser')
          .then(response => (this.user = response.data))
        
    },
});