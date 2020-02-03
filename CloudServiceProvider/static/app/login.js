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
	<section class="forms-section">
  <h1 class="section-title">Cloud Service Provider</h1>
  <div class="forms">
    <div class="form-wrapper is-active">
      <button type="button" class="switcher switcher-login">
        Login
        <span class="underline"></span>
      </button>
      <form class="form form-login">
        <fieldset>
          <legend>Please, enter your email and password for login.</legend>
          <div class="input-block">
            <label for="login-email">E-mail</label>
            <input id="login-email" type="email" v-model="email" required>
          </div>
          <div class="input-block">
            <label for="login-password">Password</label>
            <input id="login-password" type="password" v-model="password" required>
          </div>
          {{error3}}
        </fieldset>
        <button type="submit" class="btn-login" v-on:click="attemptLog()">Login</button>
		
      </form>
    </div>
    <div class="form-wrapper">
      <button type="button" class="switcher switcher-signup">
        <span class="underline"></span>
      </button>
      <form class="form form-signup">
        <fieldset>
          <legend>Please, enter your email, password and password confirmation for sign up.</legend>
          <div class="input-block">
            <label for="signup-email">E-mail</label>
            <input id="signup-email" type="email" required>
          </div>
          <div class="input-block">
            <label for="signup-password">Password</label>
            <input id="signup-password" type="password" required>
          </div>
          <div class="input-block">
            <label for="signup-password-confirm">Confirm password</label>
            <input id="signup-password-confirm" type="password" required>
          </div>
        </fieldset>
        <button type="submit" class="btn-signup">Continue</button>
      </form>
    </div>
  </div>
</section>

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