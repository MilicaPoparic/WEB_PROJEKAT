Vue.component("log-in", {
	data: function () {
		    return {
		     error1: '',
		     error2: '',
		     error3: '',
		     email: '',
		     password:''
		    }
	},
	template: ` 
	
		<div>
			<h3>Login</h3>
			<table>
				<tr>
					<input type="text" style="width:60px" size="5" v-model="email" name="username"> {{error1}}

				</tr>
				<tr>
					<input type="password" style="width:60px" size="5" v-model="password" name="password"> {{error2}}
				</tr>
				<tr>	
					<button v-on:click="attemptLog">Login</button>
				</tr>
				{{error3}}
		</table>
		
		</div>	     
		`,
		methods: {
			attemptLog: function () {
			  if(!this.email){
				  this.error1 = 'Email is required!'
			  }else {this.error1=''}
			  if(!this.password){
				  this.error2 = 'Password is required!'
			  }else {this.error2=''}
			  if(this.email && this.password)
			  {
				  axios
			      .post('rest/login', {"email":this.email, "password":this.password})
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error3 = '';
			    		  location.href = '#/h';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error3 = 'Wrong email or password!';
			    	  this.error1='';
			    	  this.error2='';
			      })
			   }
			  
			}
		  },
			mounted () {
		        axios
		          .get('rest/testLogin')
		          .then((response) => {
					    	  if(response.status == 200) {
					    		  location.href = '#/h';
					    		  //ako je status 200 treba usput i da dobavi podatke koje ce da prikaze za vm 
					    	  }
					      })
					      .catch((response)=>{
					    	  location.href = '#/';
					      })
		  },
});