Vue.component("addDr",{
	data: function(){
		return{
			name:'',
			capacity:'',
			types:{},
			organizat:'',
			vms:{},
			orgs:{},
			driveType:'',
			nameVM:'',
			error1:'',
			error2:'',
			error23:'',
			error3:'',
			error4:'',
			role:''
			
		}
	},
	template:
	`
	<div>
		
	<p>Add drive:</p>
	<br>
	<table class="table">
	<tr>
		<td>
	        Name 
	    </td>
	    <td>
	    	<input type="text"  v-model="name" name="name">
	    </td>
	    
	    {{error1}}
	   
	</tr>
	<tr>
		<td>
	    	Drive type
	    </td>
	    <td>
			<select v-model="driveType">
				<option default value=""> -Select- </option>
	       		<option v-for="(val, key) in types" :value="key" >{{key}}</option>
			</select>
	    </td>
	    {{error2}} 	
	</tr>
	<tr>
		<td>
	    	Organization 
	    </td>
	    <td v-if="role =='superAdmin'">
	    	<select v-model="organizat">
				<option default value=""> -Select-</option>
	       		<option v-for="v in orgs" :value="v" >{{v.name}}</option>
			</select>
	    </td>
	    <td v-if="role=='admin'">
	     	{{orgs}}
	    </td>
	    {{error23}}
	</tr>
	
	
	<tr>
		<td>
	    	Capacity 
	    </td>
	    <td>
	    	<input type="number"  v-model="capacity" name="capacity">
	    </td>
	    {{error3}}
	</tr>
	<tr>
		<td>
	    	Name of VM 
	    </td>
	    <td v-if="role =='superAdmin'">
	    	<select v-model="nameVM">
				<option default value=""> -Select- </option>
	       		<option v-for="v in vms":value="v.name"  v-if="v.nameOrg == organizat.name" >{{v.name}}</option>
			</select>
	    </td>
	    <td v-if="role =='admin'">
	    	<select v-model="nameVM">
				<option default value=""> -Select- </option>
	       		<option v-for="v in vms" :value="v.name">{{v.name}}</option>
			</select>
	    </td>	
	</tr>	
	</table>
	<br>
	
	<button v-on:click="addD">Add</button>
	 {{error4}}
</div>	
	`
	,
	methods:{
		addD:function(){
			this.error1='';
			this.error2='';
	        this.error23='';
	        this.error3='';
			this.error4= '';
			if(!this.name){
				this.error1='Name of drive is required!';
			}
			if(!this.driveType){
				this.error2='Name of type is required!';
			}
			if(!this.capacity){
				
				this.error3='Value of capacity is required!';
			}if(!this.organizat && this.role =='superAdmin'){
				
				this.error23='Organization is required!';
			}
			if(!this.nameVM){
				this.nameVM =null;
			}
			if(this.role =='admin'){
				if(this.name && this.driveType && this.capacity && this.orgs)
				{
					
					let drive ={
						"name":this.name, "driveType":this.driveType,"nameOrg": this.orgs,
						"capacity":this.capacity, "nameVM":this.nameVM,
					}
					axios
				      .post("rest/addNewDrive", drive)
				      .then((response) => {
				    	  if(response.status == 200) {
				    		  this.error4= '';
				    		  location.href = '#/drives'; 
				    	  }
				      })
				      .catch((response)=>{
				    	  this.error1='';
				    	  this.error2='';
				    	  this.error23='';
				    	  this.error3='';
				    	  this.error4 = 'Wrong values!';
				      })
				}	
				
				
			}if(this.role =='superAdmin')
			{
				if(this.name && this.driveType && this.capacity && this.organizat)
				{
					
					let drive ={
						"name":this.name, "driveType":this.driveType,"nameOrg": this.organizat.name,
						"capacity":this.capacity, "nameVM":this.nameVM,
					}
					axios
				      .post("rest/addNewDrive", drive)
				      .then((response) => {
				    	  if(response.status == 200) {
				    		  this.error4= '';
				    		  location.href = '#/drives'; 
				    	  }
				      })
				      .catch((response)=>{
				    	  this.error1='';
				    	  this.error2='';
				    	  this.error23='';
				    	  this.error3='';
				    	  this.error4 = 'Wrong values!';
				      })
				}	
				
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
			  	        		location.href = '#/ad';
			  				    }
			  				   })
			  				   .catch((response)=>{
			  				    	location.href = '#/forbidden';
			  				      })
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      });
	
		axios
        .get('rest/virtualne')
        .then((response) =>(this.vms = response.data));
		axios
      	.get('rest/getDTypes')
      	.then(response => (this.types = response.data));
		axios
      	.get('rest/getOrganizationss')
      	.then(response => (this.orgs = response.data));
		axios
	        .get('rest/getRole')
	        .then(response => (this.role = response.data));
    },
});