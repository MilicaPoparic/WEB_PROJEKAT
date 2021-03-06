Vue.component("change-org",{
	data: function(){
		return{
			org: {},
			nameErr: '',
		    captionErr: '',
		    logoErr: '',
		    role: ''
		}
	},
	template:
		`
<div>
		<p>Organizacije:</p>
		<table>
		<tr>
				<td> Name: </td>
				<td><input type="text" style="width:60px" size="3" v-model="org.name" name="name">{{nameErr}}</td> 
		</tr>
			
		<tr >	
				<td> Caption: </td>
				<td><input type="text" style="width:60px" size="3" v-model="org.caption" name="caption">{{captionErr}}</td> 
		</tr>
		
		<tr >
				<td v-if="role=='superAdmin'"> Upload logo: </td>
				<td><input type="file" v-if="role=='superAdmin'" @change = "onUpload" ></td> 
				
		</tr>
		<tr>
			<td><button v-on:click="change()">Change data</button></td> 
		</tr>
		</table>
		

</div>
			`
,
	methods : {
		change : function() {
			if(!this.org.name){
				this.nameErr = 'Name is required!'
			}
			if(!this.org.caption){
				this.captionErr = 'Caption is required!'
			}
			if (this.org.name && this.org.caption) {
				axios
				.post('rest/changeOrg', this.org)
				.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/o';
		    	  }
		      })
				.catch(response=>this.nameErr = 'Name must me unique!')
			}
		},
		onUpload(event) {
			this.org.logo = (event.target.files)[0].name;
		}
	},
	mounted () {	
	     axios
	        .get('rest/testLogin')
	        .then((response) => {
				    	  if(response.status == 200) {
				    		  axios
				  	        .get('rest/checkSuperAdminAdmin')
				  	        .then((response) => {
				  	        	if(response.status == 200) {
				  	        		location.href = '#/changeOrg';
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
          .get('rest/getOrganization')
          .then(response => (this.org = response.data))
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));
    },
});