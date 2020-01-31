Vue.component("detailCateg",{
	data: function(){
		return{
			category:{},
			nameID:'',
			numCPU:'',
			numRAM:'',
			numGPU:'',
			error1:'',
			error2:''
		}
	},
	template:
		`
<div>
		Category:
		<br>
		<table border="1" class="table">
		<tr>
				<td> Name: </td>
				<td>{{category.name}}</td>
		</tr>
			
		<tr >	
				<td> CORE: </td>
				<td>{{category.coreNumber}}</td>
		</tr>
			
		<tr>
				<td> RAM: </td>
				<td>{{category.RAM}}</td>

		</tr>
			
		<tr >
				<td> GPU </td>
				<td>{{category.GPUcores}}</td>
		</tr>
        </table>
		<p>Change:</p>
		<br>
		<table class="table">
		<tr>
			<td>
		    	Name 
		    </td>
		    <td>
		    	<input type="text"  v-model="nameID" name="nameID">
		    </td>
		</tr>
		<tr>
			<td>
		    	CORE 
		    </td>
		    <td>
		    	<input type="number"  v-model="numCPU" name="numCPU" >
		    </td>
		</tr>
		<tr>
			<td>
		    	RAM 
		    </td>
		    <td>
		    	<input type="number" v-model="numRAM" name="numRAM" >
		    </td>
		</tr>
		<tr>
			<td>
		    	GPU 
		    </td>
		    <td>
		    	<input type="number"  v-model="numGPU" name="numGPU" > 
		    </td>	
		</tr>	
		<tr><button v-on:click="change()">Change</button> </tr>	
		</table>
		{{error1}}
		<br>
		<button v-on:click="removeCategory()">Delete</button>{{error2}}
</div>
			`
,
	methods : {
		change : function() {
			if(this.nameID || this.numCPU || this.numRAM ||this.numGPU){
				if(!this.nameID){
					this.nameID=null;
				}if(!this.numCPU){
					this.numCPU=0;
				}if(!this.numRAM){
					this.numRAM=0;
				}if(!this.numGPU){
					this.numGPU=0;
				}
				let obj =
					{
					 "name":this.nameID, "coreNumber":this.numCPU,"RAM":this.numRAM,"GPUcores":this.numGPU
					}
				axios
			      .post('rest/forChange',obj)
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error1 = '';
			    		  location.href = '#/c';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1 = 'The data is invalid!';
			      })
			} 

			if(!this.numGPU && !this.nameID && !this.numCPU && !this.numRAM ){
				location.href = '#/c';
			}
		},
		removeCategory: function(){
			axios
		      .post('rest/removeCategory', this.category)
		      .then((response) => {
		    	  if(response.status == 200) {
		    		  this.error2 = '';
		    		  location.href = '#/c';
		    	  }
		      })
		      .catch((response)=>{
		    	  this.error2 = 'Couldnt remove category!';
		      })
		}
	}
	,
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
			  	        		location.href = '#/d';
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
		.get('rest/getCategory')
		.then(response => (this.category = response.data))
    },
});