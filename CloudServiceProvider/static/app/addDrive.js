Vue.component("addDr",{
	data: function(){
		return{
			name:'',
			capacity:0,
			driveType:'',
			nameVM:'',
			error1:'',
			error2:'',
			error3:'',
			error4:''
			
		}
	},
	template:
	`
	<div>
		
	Add drive:
	<table border="1">
	<tr>
		<td>
	        Name 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="name" name="name">
	    </td>
	    
	    {{error1}}
	   
	</tr>
	<tr>
		<td>
	    	Drive type
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="driveType" name="driveType">
	    </td>
	    {{error2}} 	
	</tr>
	<tr>
		<td>
	    	Capacity 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="capacity" name="capacity">
	    </td>
	    {{error3}}
	</tr>
	<tr>
		<td>
	    	Name of VM 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="nameVM" name="nameVM"> 
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
			if(!this.name){
				this.error1='Name of drive is required!';
			}
			if(!this.driveType){
				this.error2='Name of type is required!';
			}
			if(!this.capacity){
				this.error3='Value of capacity is required!';
			}
			if(!this.nameVM){
				this.nameVM =null;
			}
			if(this.name && this.driveType && this.capacity)
			{
				axios
			      .post("rest/addNewDrive", {"name":this.name, "driveType":this.driveType, "capacity":this.capacity, "nameVM":this.nameVM })
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error4= '';
			    		  location.href = '#/drives'; 
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error4 = 'Wrong values!';
			      })
			}	
		}
	},
	mounted () {
         axios
      	.get('rest/checkRole')
      	.then((response) => {
			    	  if(response.status == 403) {
			    		  location.href = '#/forbidden';
			    	  }
			      })
		.catch((response)=>{
			location.href = '#/forbidden';
					  })
    },
});