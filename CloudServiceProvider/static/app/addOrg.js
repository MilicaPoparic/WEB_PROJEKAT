Vue.component("add-org", {
	data: function () {
	    return {
	     name: '',
	     nameErr: '',
	     caption: '',
	     captionErr: '',
	     logo: ''
	    }
},
	template: ` 
<div>
	<p>ADDING ORGANIZATION</p>
	<table>
		<tr><td>Name:</td><td><input type="text" style="width:60px" size="5" v-model="name" name="name"> {{nameErr}}</td></tr>
		<tr><td>Caption:</td><td><input type="text" style="width:60px" size="5" v-model="caption" name="caption"></td> {{captionErr}}</tr>
		<tr><td>Logo:</td><td><input type="text" style="width:60px" size="5" v-model="logo" name="logo"></td> </tr>
		<tr><button v-on:click="add">Add</button></tr>
	</table>
</div>		  
`
		
	, 
	methods : {
		add : function() {
		if(!this.name){
			this.nameErr = 'Name is required!'
		}
		if(!this.caption){
			this.captionErr = 'Caption is required!'
		}
		//sad kao ako nema url da mu ja dam neki default al to cu kad budem imala ucitavanje slike 
		if(this.name && this.caption) {
			axios
			.post('rest/addOrganization', {"name":this.name, "caption":this.caption, "logo":this.logo})
			.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/o';
		    	  }
		      })
			.catch(response=> this.nameErr='Name must me unique!')
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
				  	        		location.href = '#/addOrg';
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
	},

});