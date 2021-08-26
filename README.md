# PUpdater

**Description**: This is the lib to check your minecraft plugin update on spigot and download it.

**Usage**:

     1. Add the PUpdater.jar to your project and minecraft server.
     2. Create a new PUpdater to start using:	  
          PUpdater pupdate = new PUpdater(plugin);     		 
     3. Setup your pupdate with some attribute:  
          pupdate.setResourceId(your_plugin_id_on_spigot);
          pupdate.setAction(action);         
     4. Start checking:
          pupdate.RunChecking();
		
	  P/s: If you set Action to CHECKnDOWNLOAD, you need to set download path:
          pupdate.setPath(your_path);
			 
**Use AutoCheck**:

	To start autocheck mode, you need:
		1. Set AutoCheck to true:
			pupdate.setAutoCheck(true);
		2. Set time between each check:
			pupdate.setTimetocheck(time);
		3. Start AutoCheck:
                    pupdate.StartAutoCheck();   
         
      P/s: To stop autocheck mode, please use pupdate.setAutoCheck(false)
        
 **Important**:
 
 	This is the beta version, so there may be some errors. If you find it, please contact me to fix it. Thanks!



