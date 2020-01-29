Vue.component("report",{
	data: function(){
		return{
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
				 
				
				
		
</div>	`
,
	methods : {
		find : function() {
			//samo ako je oba popunio
			if(this.date1 && this.date2){
			axios
			.post('rest/findReport',  {"start":this.date1, "end":this.date2})
			.then((response) => {
	    	  if(response.status == 200) {
	    		  this.vm = response.data;
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