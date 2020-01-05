Vue.component("addCateg",{
	data: function(){
		return{
			nameID:'',
			numCPU:0,
			numRAM:0,
			numGPU:0,
			error1:'',
			error2:'',
			error3:'',
			error4:''
		}
	},
	template:
	`
	<div>
		
	Dodaj kategoriju:
	<table border="1">
	<tr>
		<td>
	    	Naziv kategorije 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="nameID" name="nameID">
	    </td>
	    
	    {{error1}}
	   
	</tr>
	<tr>
		<td>
	    	Broj jezgara 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="numCPU" name="numCPU">
	    </td>
	    {{error2}} 	
	</tr>
	<tr>
		<td>
	    	RAM 
	    </td>
	    <td>
	    	<input type="text" style="width:60px" size="5" v-model="numRAM" name="numRAM">
	    </td>
	    {{error3}}
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
	
	<button v-on:click="addC">Dodaj kategoriju</button>
	 {{error4}}
</div>	
	`
	,
	methods:{
		addC:function(){
			if(!this.nameID){
				this.error1='Name of categoria is required!';
			}
			if(!this.numCPU){
				this.error2='Number of cores is required!';
			}
			if(!this.numRAM){
				this.error3='Number of ram is required!';
			}
			if(this.nameID && this.numCPU && this.numRAM)
			{
				axios
			      .post("rest/addNewCategory", {"nameID":this.nameID, "numCPU":this.numCPU, "numRAM":this.numRAM, "numGPU":this.numGPU })
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error4= '';
			    		  location.href = '#/c'; 
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error4 = 'Wrong ID, number of cores or number of ram!';
			      })
			}	
		}
	},
});