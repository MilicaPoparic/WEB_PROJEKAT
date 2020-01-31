Vue.component("detailDr",{
	data: function(){
		return{
			drive:{},
			vms:{},
			types:{},
			nameVMS:'',
			error1:'',
			capacity:'',
			name: '',
			driveType:'',
			error2:'',
			error3:'',
			role: ''
		}
	},
	template:
		`
<div>
		<p>Drive:</p>
		<table class="table" border="1">
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
			
		<tr v-if="drive.nameVM!=null">
				<td> Name of VM: </td>
				<td>{{drive.nameVM}}</td>
		</tr>
        </table>
        <br>
		<table v-if="role!='user'" class="table">
		Change:
		<tr>
			<td>
		    	Name 
		    </td>
		    <td>
		    	<input type="text" style="width:84.5px" v-model="name" name="name">
		    </td>
		</tr>
		<tr>
			<td>
		    	Type 
		    </td>
		    <td>
		    	<select v-model="driveType"> 
					<option default value=""> -Select- </option>
	       			<option v-for="(val, key) in types" :value="key" >{{key}}</option>
				</select>
		    </td>
		</tr>
		<tr>
			<td>
		    	Capacity 
		    </td>
		    <td>
		    	<input type="number" style="width:84.5px" v-model="capacity" name="capacity" >
		    </td>
		</tr>
		<tr>
			<td>
		    	Name of VM  
		    </td>
		    <td>
		    	<select v-model="nameVMS">
	       			<option default value="delete"> -Select- </option>
			        <option v-for="v in vms" :value="v.name" v-if="v.nameOrg==drive.nameOrg">{{ v.name }}</option>
				</select>
		    </td>	
		</tr>	
		</table>
		<br>
		
		<button v-if="role!='user'" v-on:click="change()">Change</button> {{error1}}	
		<br>
		<button v-if="role=='superAdmin'" v-on:click="removeDrive()">Remove</button>{{error2}}
		<br>
		<button v-on:click="back()">Back</button>
</div>
			`
,
	methods : {
		change : function() {
			if(!this.name && !this.nameVMS && !this.driveType && !this.capacity ){
				location.href = '#/drives';
			}
			if(this.name || this.driveType || this.capacity ||this.nameVMS){
				this.error1='';
				if(!this.name){
					this.name=null;
				}if(!this.driveType){
					this.driveType=null;
				}if(!this.capacity){
					this.capacity=0;
				}if(!this.nameVMS){
					this.nameVMS=null;
				}
				let obj={
				 "name":this.name, "driveType":this.driveType,"nameOrg":this.drive.nameOrg,"capacity":this.capacity,
				 "nameVM":this.nameVMS
				}
				axios
			      .post('rest/forChangeDrive', obj)
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error1 = '';
			    		  location.href = '#/drives';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1 = 'The data is invalid!';
			      })
			
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
		back: function(){
             location.href = '#/drives';
		}
	}
	,
	mounted () {
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/detailDrive';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
          .get('rest/getDrive')
          .then(response => (this.drive = response.data));
		axios
          .get('rest/virtualne')
          .then(response => (this.vms = response.data));
		axios
		  .get('rest/getDTypes')
	      .then(response => (this.types = response.data));
		 axios
         .get('rest/getRole')
         .then(response => (this.role = response.data));
		  }
	
});