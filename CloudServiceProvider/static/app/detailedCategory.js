Vue.component("detailCateg",{
	data: function(){
		return{
			category:null,
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
		Kategorija:
		<table>
		<tr>
				<td> Naziv kategorije: </td>
				<td>{{category.name}}</td>
		</tr>
			
		<tr >	
				<td> Broj jezgara: </td>
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
		<table border="1">
		Izmeni:
		<tr>
			<td>
		    	Naziv kategorije 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5"   v-model="nameID" name="nameID">
		    </td>
		</tr>
		<tr>
			<td>
		    	Broj jezgara 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="numCPU" name="numCPU" >
		    </td>
		</tr>
		<tr>
			<td>
		    	RAM 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="numRAM" name="numRAM" >
		    </td>
		</tr>
		<tr>
			<td>
		    	GPU 
		    </td>
		    <td>
		    	<input type="text" style="width:60px" size="5" v-model="numGPU" name="numGPU" > 
		    </td>	
		</tr>	
		</table>
		<br>
		
		<button v-on:click="change()">Izmeni kategoriju</button> {{error1}}	
		<br>
		<button v-on:click="removeCategory()">Obrisi kategoriju</button>{{error2}}
</div>
			`
,
	methods : {
		change : function() {
			if(this.nameID || this.numCPU || this.numRAM ||this.numGPU){
				if(!this.nameID){
					this.nameID="null";
				}if(!this.numCPU){
					this.numCPU=0;
				}if(!this.numRAM){
					this.numRAM=0;
				}if(!this.numGPU){
					this.numGPU=0;
				}
				axios
			      .post('rest/forChange', {"nameID":this.nameID, "numCPU":this.numCPU,"numRAM":this.numRAM,"numGPU":this.numGPU})
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
				//false znaci da je prazno
					axios
				      .post('rest/category', "")
				      .then(response => location.href = '#/c');
			}
		},
		removeCategory: function(){
			axios
		      .post('rest/removeCategory', {"category":this.category})
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
          .get('rest/getCategory')
          .then(response => (this.category = response.data))
    },
});