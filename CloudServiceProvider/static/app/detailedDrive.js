Vue.component("detailDr",{
	data: function(){
		return{
			drive:null,
			error1:'',
			error2:'',
			error3:'',
			role: ''
		}
	},
	template:
		`
<div>
		Drive:
		<table>
		<tr>
				<td> Name : </td>
				<td>{{drive.name}}</td>
		</tr>
			
		<tr >	
				<td> Type : </td>
				<td>{{drive.driveType}}</td>
		</tr>
			
		<tr>
				<td> Capacity: </td>
				<td>{{drive.capacity}}</td>

		</tr>
			
		<tr >
				<td> Name of VM: </td>
				<td>{{drive.nameVM}}</td>
		</tr>
        </table>
		<table border="1">
		Change:
		<tr>
			<td>
		    	Name 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5"   v-model="name" name="name">
		    </td>
		</tr>
		<tr>
			<td>
		    	Type 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="driveType" name="driveType" >
		    </td>
		</tr>
		<tr>
			<td>
		    	Capacity 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="capacity" name="capacity" >
		    </td>
		</tr>
		<tr>
			<td>
		    	Name of VM  
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="nameVM" name="nameVM" > 
		    </td>	
		</tr>	
		</table>
		<br>
		
		<button v-if="role!='user'" v-on:click="change()">Change</button> {{error1}}	
		<br>
		<button v-if="role=='superAdmin'" v-on:click="removeDrive()">Remove</button>{{error2}}
		<br>
		<button v-if="role=='admin'" v-on:click="switchVM()">Switch on/off VM</button>{{error3}}
</div>
			`
,
	methods : {
		change : function() {
			if(this.name || this.driveType || this.capacity ||this.nameVM){
				if(!this.name){
					this.name="null";
				}if(!this.driveType){
					this.driveType="null";
				}if(!this.capacity){
					this.capacity=0;
				}if(!this.nameVM){
					this.nameVM="null";
				}
				axios
			      .post('rest/forChangeDrive', {"name":this.name, "driveType":this.driveType,"capacity":this.capacity,"nameVM":this.nameVM})
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error1 = '';
			    		  location.href = '#/drives';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1 = 'The data is invalid!';
			    	  console.log(this.name);
			    	  console.log(this.driveType);
			    	  console.log(this.capacity);
			    	  console.log(this.nameVM);
			      })
			} 
			if(!this.name && !this.nameVM && !this.driveType && !this.capacity ){
				//false znaci da je prazno
					axios
				      .post('rest/viewDrives', "")
				      .then(response => location.href = '#/drives');
			}
		},
		removeDrive: function(){
			axios
		      .post('rest/removeDrive',this.drive)
		      .then((response) => {
		    	  if(response.status == 200) {
		    		  this.error2 = '';
		    		  location.href = '#/drives';
		    	  }
		      })
		      .catch((response)=>{
		    	  this.error2 = 'Couldnt remove drive!';
		      })
		},
		switchVM: function(){
			alert("Not implemented yet.SWITCH OFF ON VM");
		} 
	}
	,
	mounted () {
	
        axios
          .get('rest/getDrive')
          .then(response => (this.drive = response.data));
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
    }
});