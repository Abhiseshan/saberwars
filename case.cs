
string data[] = received.split(",", null);

switch (data[0]){
	case "values":
		if (data[1] == "P1"){
			player1.rotation = new Vector3(Float.tryParse(data[5]), Float.tryParse(data[6]), Float.tryParse(data[7]));
			player1.postion = new Vector3(Float.tryParse(data[2]), Float.tryParse(data[3]), Float.tryParse(data[4]));
		}
		
		if (data[1] == "P2"){
			player2.rotation = new Vector3(Float.tryParse(data[5]), Float.tryParse(data[6]), Float.tryParse(data[7]));
			player2.postion = new Vector3(Float.tryParse(data[2]), Float.tryParse(data[3]), Float.tryParse(data[4]));
		}
		
		break;
	
	case "saber":
		if (data[1] == "P1"){
			player1.lightSaberOn = Boolean.tryParse(data[2]);
		}
		
		if (data[1] == "P2"){
			player2.lightSaberOn = Boolean.tryParse(data[2]);
		}
		
		break;
		
	case "name":
		if (data[1] == "P1"){
			player1.name = data[2];
		}
		
		if (data[1] == "P2"){
			player2.name = data[2];
		}
		
		break;
		
	case "saberColor":
		if (data[1] == "P1"){
			player1.setSaberColor = Integer.tryParse(data[2]);
		}
		
		if (data[1] == "P2"){
			player2.setSaberColor = Integer.tryParse(data[3]);
		}
		
		break;
	
	default:
		Log(default);
}