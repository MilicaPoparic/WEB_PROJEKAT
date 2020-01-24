Vue.component("addCateg",{
	data: function(){
		return{
			nameID:'',
			numCPU:'',
			numRAM:'',
			numGPU:'',
			error1:'',
			error2:'',
			error3:'',
			error4:''
		}
	},
	template:
	`
	<div>
		
	Add category:
	<br>
	<table>
	<tr>
		<td>Name</td>
	    <td><input type="text" style="width:60px" size="5" v-model="nameID" name="nameID">{{error1}}</td>
	</tr>
	<tr>
		<td>
	    	CORE
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="numCPU" name="numCPU">{{error2}}
	    </td>
	</tr>
	<tr>
		<td>
	    	RAM 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="numRAM" name="numRAM"> {{error3}}
	    </td>
	</tr>
	 
	<tr>
		<td>
	    	GPU 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="numGPU" name="numGPU"> 
	    </td>	
	</tr>	
	</table>
	<br>
	
	<button v-on:click="addC">Add</button>
	 {{error4}}
</div>	
	`
	,
	methods:{
		addC:function(){
			if(!this.nameID){
				this.error1='Name of categoria is required!';
			}else {this.error1='';}
			if(!this.numCPU){
				this.error2='Number of cores is required!';
			}else {this.error2='';}
			if(!this.numRAM){
				this.error3='Number of ram is required!';
			}else {this.error3='';}
			if(this.nameID && this.numCPU && this.numRAM)
			{
				this.error4='';
				axios
			      .post("rest/addNewCategory", {"nameID":this.nameID, "numCPU":this.numCPU, "numRAM":this.numRAM, "numGPU":this.numGPU })
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error4= '';
			    		  location.href = '#/c'; 
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1='';
			    	  this.error2='';
			    	  this.error3='';
			    	  this.error4 = 'Wrong ID, number of cores or number of ram!';
			      })
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
			  	        .get('rest/checkSuperAdmin')
			  	        .then((response) => {
			  	        	if(response.status == 200) {
			  	        		location.href = '#/ac';
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