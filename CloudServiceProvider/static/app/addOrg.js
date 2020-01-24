Vue.component("add-org", {
	data: function () {
	    return {
	     name: '',
	     nameErr: '',
	     caption: '',
	     captionErr: '',
	     logoErr: '',
	     logo: ''
	    }
},
	template: ` 
<div>
	<p>ADDING ORGANIZATION</p>
	<table>
		<tr><td>Name:</td><td><input type="text" style="width:60px" size="5" v-model="name" name="name"> {{nameErr}}</td></tr>
		<tr><td>Caption:</td><td><input type="text" style="width:60px" size="5" v-model="caption" name="caption"> {{captionErr}}</td> </tr>
		<tr >
				<td> Upload logo: </td>
				<td><input type="file" @change = "onUpload" ></td> 
				
		</tr>
		<tr><td>Logo url:</td><td><input type="text" style="width:60px" size="5" v-model="logo" name="logo"></td> </tr>
		{{logoErr}}
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
		
		if(this.name && this.caption && this.logo) {
			axios
			.post('rest/addOrganization', {"name":this.name, "caption":this.caption, "logo":this.logo})
			.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/o';
		    	  }
		      })
			.catch(response=> this.nameErr='Name must me unique!')
			}
		else {
			this.logoErr = 'Logo required';
		}
		},
		onUpload(event) {
			this.logo = (event.target.files)[0].name;
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