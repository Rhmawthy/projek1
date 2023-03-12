<?php

	if (isset ($_POST['Input'])){
		$jumlah = $_POST['jumlah'];
		echo "jumlah semen : $jumlah <b>";

		$hasil = $jumlah * 60000;
		echo "total harga = $hasil <br>";

	    if ($hasil >= 200){
			echo"anda mendapatkan diskon 5%";
		}
	}



		
    

    /*$hasil = $jumlah * 60000;

    echo "total harga = $hasil <br>";
    
    if ($hasil >= 200){
    	echo "anda mendapatkan diskon 5%";
    }
    else if ($hasil >= 100){
    	echo "anda mendapatkan diskon 2%";
    }
    else{
    	echo "tidak ada diskonnnn";
    }*/
		
	

?>