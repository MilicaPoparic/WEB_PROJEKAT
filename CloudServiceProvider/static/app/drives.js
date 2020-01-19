Vue.component("drive",{
	data: function(){
		return{
			drivess: null,
			types: null,
			names: null,
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
	
	<table border="1">
		<tr>
			<td> Name: </td>
			<td><input type="text" v-model="name" name="name"></td>
		</tr>
		
		<tr>
			<td> Category: </td>
			<td><input type="text" style="width:60px" size="5" v-model="fromm" name="fromm">
			-
			<input type="text" style="width:60px" size="5" v-model="too" name="too"></td>
		</tr>
		
		<tr>
			<td> Type: </td>
			<td>
				<div id='drop-down' v-for="(val,k) in types">
				  <input type="radio" :id="val" :value="k" v-model="checkedNames1">
				  <label for = "k" >{{k}}</label>
				  <br>
				</div>
			</td>
		</tr>
		<tr>
			<td> Name VM:</td>
			<td>
				<div id='drop-down2' v-for="(v,key) in names">
					<input type="radio" :id="v" :value="key" v-model="checkedNames2">
					<label for = "key" >{{key}}</label>
					<br>
				</div>
			</td>
		</tr>
			
	</table>
	<button v-on:click="search()">Search</button>{{error1}}

</div>
`
  ,
  
	methods : {
		search: function(){
			if(!this.name && !this.fromm && !this.too && !this.checkedNames1 && !this.checkedNames2){
				alert("Unesite parametre pretrage");
			}
			else{
				if(!this.name){
					this.name="null";
				}if(!this.fromm){
					this.fromm=0;
				}if(!this.too){
					this.too=0;
				}if(!this.checkedNames1){
					this.checkedNames1="null";
				}
				if(!this.checkedNames2){
					this.checkedNames2="null";
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