
string data[] = received.split(",", null);

switch (data[0]){
	case "values":
		if (data[1] == "P1"){
			player1.rotation = new Vector3(data[5], data[6], data[7]);
			player1.postion = new Vector3(data[2], data[3], data[4]);
		}
		
		if (data[1] == "P2"){
			player2.rotation = new Vector3(data[5], data[6], data[7]);
			player2.postion = new Vector3(data[2], data[3], data[4]);

		}
		
		break;
	
	case "saber":
		if (data[1] == "P1"){
			player1.lightSaberOn = (bool) data[2];
		}
		
		if (data[1] == "P2"){
			player2.lightSaberOn = (bool) data[2];
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
			player1.setSaberColor = data[2];
		}
		
		if (data[1] == "P2"){
			player2.setSaberColor = data[3];
		}
		
		break;
	
	default:
		Log(default);
}