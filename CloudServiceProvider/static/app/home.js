Vue.component("home-page", {
	template: ` 
<div>
		<button v-on:click="logout">Logout</button>
</div>		  
`
	, 
	methods : {
		logout : function() {
			axios
		      .post('rest/logout', "nesto")
		      .then(response => location.href = '#/');

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