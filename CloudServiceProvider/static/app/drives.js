Vue.component("drive",{
	data: function(){
		return{
			drivess: {},
			types: {},
			names: {},
			fromm:'',
			too:'',
			name:'',
			checkedNames1:'',
			checkedNames2:'',
			error1:'',
			role: ''
		}
	},
	template:
	`
<div>
	View drives:
	<table border="1">
	<tr bgcolor="gray">
			<th> Name </th>
			<th> Capacity </th>
			<th> VM </th>
	</tr>
		
	<tr v-for="d in drivess">
			<td><a href="#" v-bind:class="goToDetail" @click="goToDetail(d)">{{d.name}}</a></td>
			<td>{{d.capacity}}</td>
			<td>{{d.nameVM}}</td>
	</tr>
	</table>
	<br>
	<button v-if="role!='user'" v-on:click="addDrive()">Add drive</button>	
	
	<br>
	<br>
	Find a drive:
	<br>
	<table border ="1">
		<tr>
			<td> Name: </td>
			<td><input type="text" v-model="name" name="name"></td>
		</tr>
		
		<tr>
			<td> Category: </td>
			<td><input type="number" style="width:92px" v-model="fromm" name="fromm">
			-
			<input type="number" style="width:92px"  v-model="too" name="too"></td>
		</tr>
		
		<tr>
			<td> Type: </td>
			<td>
				<select v-model="checkedNames1">
					<option default value=""> -Select type- </option>
	       			<option v-for="(val,k) in types" :value="k" >{{k}}</option>
				</select>
			</td>
		</tr>
		<tr>
			<td> Name VM:</td>
			<td>
				<select v-model="checkedNames2">
					<option default value=""> -Select vm- </option>
	       			<option v-for="(v,key) in names" :value="key" >{{key}}</option>
				</select>
			</td>
			
		</tr>
	</table>
	<br>
	<button v-on:click="search()">Search</button>{{error1}}
</div>
`
  ,
  
	methods : {
		search: function(){
			if(!this.name && !this.fromm && !this.too && !this.checkedNames1 && !this.checkedNames2){
				alert("Enter the search parameters!");
			}
			else{
				if(!this.name){
					this.name=null;
				}if(!this.fromm){
					this.fromm=0;
				}if(!this.too){
					this.too=0;
				}if(!this.checkedNames1){
					this.checkedNames1=null;
				}
				if(!this.checkedNames2){
					this.checkedNames2=null;
				}
				axios
			      .post('rest/filterDrive', {"name":this.name, "fromm":this.fromm,"too":this.too, "checked1":this.checkedNames1, "checked2":this.checkedNames2 })
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error1 ='';
			    		  location.href = '#/resultSearchingDrive';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1 ='No result of searching!';
			      })
			    
			}
			
		},
		addDrive: function(){
			location.href = '#/ad';
		},
	    goToDetail:function(d){
			axios
		      .post('rest/detailDrive', d)
		      .then(response => location.href = '#/detailDrive');
	    }
	},
	mounted(){
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/drives';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
		 axios
      	.get('rest/getDrives')
      	.then(response => (this.drivess = response.data));
      	
      	axios
      	.get('rest/getDTypes')
      	.then(response => (this.types = response.data));
      	
      	axios
      	.get('rest/getDVM')
      	.then(response => (this.names = response.data))
	}

});