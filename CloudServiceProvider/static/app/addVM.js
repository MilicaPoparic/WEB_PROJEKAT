Vue.component("add-vm",{
	data: function(){
		return{
			name:'',
			numCPU:0,
			numRAM:0,
			numGPU:0,
			categories:null,
			organizations:null,
			drives: null,
			nameOrg:'',
			nameC:'',
			nameD:[],
			error1:'',
			error2:'',
			error3:'',
			error4:'',
			role:''
		}
	},
	template:
	`
	<div>
	<p>Add new VM:</p> 
	<p v-if="role==='admin'">Organization: {{organizations}}</p>
	<table border="1">
	
	<tr v-if="role==='superAdmin'">
		<td>
	    	Organization:
	    </td>
	    <td>
	       <select v-model="nameOrg">
	       		<option value=""> -Select organization- </option>
			    <option v-for="o in organizations" :value="o.name" >{{ o.name }}</option>
			</select>
	    </td>	
	    {{error3}}
	       
	</tr>
	<tr>
		<td>
	    	Name of VM: 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="name" name="name">
	    </td>
	    
	    {{error1}}
	   
	</tr>
	<tr>
		<td>
	    	Categoria: 
	    </td>
	    <td>
	    	<select  v-model="nameC">
	    		<option value=""> -Select criteria- </option>
			    <option v-for="c in categories" v-bind:value="c.name" >{{ c.name }}</option>
			</select>
	    </td>
	    
	    {{error2}}
	   
	</tr>
	<tr v-if="nameC!=''">
		<td>
	    	Number of CPU: 
	    </td>
	    <td v-for="c in categories" v-if="c.name==nameC">
	    	{{c.coreNumber}}
	    </td>
	</tr>
	<tr v-if="nameC!=''">
		<td>
	    	Number of RAM:
	    </td>
	    <td v-for="c in categories" v-if="c.name==nameC">
	    	{{c.RAM}}
	    </td>
	</tr>
	<tr v-if="nameC!=''">
		<td>
	    	Number of GPU:
	    </td>
	    <td v-for="c in categories" v-if="c.name==nameC">
	    	{{c.GPUcores}}
	    </td>
	</tr>
	<tr v-if="role=='superAdmin'">
		<td>
	    	Add drive/s: 
	    </td>
	    <td>
			<div v-for="(v,k) in drives" v-if="v==nameOrg">
				<input type="checkbox"  v-model="nameD" :value="k">
				<label for="k">{{k}}</label>
			<br>
			</div>
	    </td>
	</tr>
	<tr v-if="role=='admin'">
		<td>
	    	Add drive/s: 
	    </td>
	    <td>
			<div v-for="(v,k) in drives" v-if="v==organizations">
				<input type="checkbox"  v-model="nameD" :value="k">
				<label for="k">{{k}}</label>
			<br>
			</div>
	    </td>
	</tr>
	</table>
	<br>
	<button v-on:click="addV">Add VM</button>
	 {{error4}}
</div>	
	`
	,
	methods:{
		addV:function(){
			if(!this.name){
				this.error1='Name of vm is required!';
			}
			if(!this.nameC){
				this.error2='Categoria is required!';
			}
			if(!this.nameOrg && this.role=="superAdmin"){
				this.error3='ORG is required!';
			}
			
			if(this.name && this.nameOrg && this.nameC)
			{
				if(!this.nameD){
					this.nameD = null;
				}
				let vm ={
					"name":this.name, "nameC": this.nameC, "nameOrg":this.nameOrg,"nameD":this.nameD
				}
				axios
			      .post("rest/addNewVM", vm)
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error4= '';
			    		  location.href = '#/h'; 
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error4 = 'Wrong input!';
			      })
			}
			if(this.name && this.nameC && this.role=="admin")
			{
				if(!this.nameD){
					this.nameD = null;
				}
				let vm ={
					"name":this.name, "nameC": this.nameC, "nameOrg":this.organizations,"nameD":this.nameD
				}
				axios
			      .post("rest/addNewVM", vm)
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error4= '';
			    		  location.href = '#/h'; 
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error4 = 'Wrong input!';
			      })
			}	
		}
	},
	mounted () {
        axios
          .get('rest/getOrganizationsForVM')   
          .then(response => (this.organizations = response.data));
        axios
          .get('rest/getCategoriesForVM')   
          .then(response => (this.categories = response.data));
        axios
          .get('rest/getDrivesForVM')   
          .then(response => (this.drives = response.data));
        axios
        .get('rest/getRole')   
        .then(response => (this.role = response.data));
    },
	
	
});