Vue.component("report",{
	data: function(){
		return{
			data: {},
			date1: '',
		    date2: '',
		    err: ''
		}
	},
	template:
		`
<div>
		<p>MONTHLY REPORT</p>
		<table>
		<tr>
				<td> Start date: </td>
				<td><input type="date" v-model="date1" name="name"></td>
		</tr>
		<tr >	
				<td> End date: </td>		
				<td><input type="date" v-model="date2" name="noname"></td> 
		</tr>
		{{err}}
		<td><button v-on:click="find()">Find</button></td>
		</table>
		
		<br>
		<table border="1">
			<tr v-for="(v,k) in data"> 
				<td>{{k}}</td>
			</tr>
			<tr v-for="(v,k) in data">
				<td>{{v}}</td>
			</tr>
		</table>
		
				 
				
				
		
</div>	`
,
	methods : {
		find : function() {
			//samo ako je oba popunio
			if(this.date1 && this.date2){
			axios
			.post('rest/findReport',  {"newStart":this.date1, "newEnd":this.date2})
			.then((response) => {
	    	  if(response.status == 200) {
	    		  this.data = response.data;
	    	  }
			})
			.catch((response)=>{
		    	  this.err='INVALID DATE!'
		      })
			this.date1=''; this.date2=''; this.err='';
			}
		}
	},
	mounted () {	
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  axios
			  	        .get('rest/checkAdmin')
			  	        .then((response) => {
			  	        	if(response.status == 200) {
			  	        		location.href = '#/report';
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
          .get('rest/getRole')
          .then(response => (this.role = response.data));
    },
});