package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.controller.validator.PerifericaValidator;
import com.example.demo.model.BuildPC;
import com.example.demo.model.Componente;
import com.example.demo.model.Periferica;
import com.example.demo.service.BuildPCService;
import com.example.demo.service.ComponenteService;
import com.example.demo.service.PerifericaService;



@Controller
public class PerifericaController {
	
	@Autowired
	private PerifericaService perifericaService;
	
    @Autowired
    private PerifericaValidator perifericaValidator;
    
	@Autowired
	private ComponenteService componenteService;
    
	@Autowired
	private BuildPCService buildService;
    
    
    @RequestMapping(value = "/periferiche", method = RequestMethod.GET)
    public String getListaPeriferiche(Model model) {
//    		model.addAttribute("listaPeriferiche", this.perifericaService.tutti());
    		
    		model.addAttribute("listaDisplay",this.perifericaService.displayPeriferiche());
    		model.addAttribute("listaCuffie",this.perifericaService.cuffiePeriferiche());
    		model.addAttribute("listaMouse",this.perifericaService.mousePeriferiche());
    		model.addAttribute("listaTastiere",this.perifericaService.tastierePeriferiche());
    		model.addAttribute("listaExtra",this.perifericaService.extraPeriferiche());
    		return "listaPeriferiche.html";
    }
    

    
    @RequestMapping(value = "/admin/periferiche", method = RequestMethod.GET)
    public String getListaPerifericheAdmin(Model model) {
//    		model.addAttribute("listaPeriferiche", this.perifericaService.tutti());
    		
    		model.addAttribute("listaDisplay",this.perifericaService.displayPeriferiche());
    		model.addAttribute("listaCuffie",this.perifericaService.cuffiePeriferiche());
    		model.addAttribute("listaMouse",this.perifericaService.mousePeriferiche());
    		model.addAttribute("listaTastiere",this.perifericaService.tastierePeriferiche());
    		model.addAttribute("listaExtra",this.perifericaService.extraPeriferiche());
    		
    		return "admin/listaPeriferiche.html";
    }
    
    @RequestMapping(value = "/admin/periferica", method = RequestMethod.GET)
    public String addPeriferica(Model model) {
    	model.addAttribute("periferica", new Periferica());
        return "admin/perifericaForm.html";
    }
    
    @RequestMapping(value = "/admin/periferica", method = RequestMethod.POST)
    public String addPeriferica(@ModelAttribute("periferica") Periferica periferica, Model model, BindingResult bindingResult) {
    	this.perifericaValidator.validate(periferica, bindingResult);
        if (!bindingResult.hasErrors()) {
        	this.perifericaService.inserisci(periferica);
//            model.addAttribute("listaPeriferiche", this.perifericaService.tutti());
        	model.addAttribute("listaDisplay",this.perifericaService.displayPeriferiche());
    		model.addAttribute("listaCuffie",this.perifericaService.cuffiePeriferiche());
    		model.addAttribute("listaMouse",this.perifericaService.mousePeriferiche());
    		model.addAttribute("listaTastiere",this.perifericaService.tastierePeriferiche());
    		model.addAttribute("listaExtra",this.perifericaService.extraPeriferiche());
            return "admin/listaPeriferiche.html";
        }
        return "admin/perifericaForm.html";
    }
    
    @RequestMapping(value = "/periferica/{id}", method = RequestMethod.GET)
    public String getPeriferica(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("periferica", this.perifericaService.perifericaPerId(id));
    	return "Periferica.html";
    }
    
    @RequestMapping(value = "/admin/periferica/{id}", method = RequestMethod.GET)
    public String getPerifericaAdmin(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("periferica", this.perifericaService.perifericaPerId(id));
    	return "admin/Periferica.html";
    }
    
    @GetMapping("/buildPeriferica/{buildId}/{perifericaId}")
    public String impostaPeriferica(@PathVariable("buildId") Long buildId, @PathVariable("perifericaId") Long perifericaId, Model model) {
        BuildPC build = buildService.buildPerId(buildId);
        Periferica periferica = perifericaService.perifericaPerId(perifericaId);

        //set prezzo
        Float prezzoFinale = build.getPrezzoTotale() + periferica.getPrezzo(); //uguale a 0 all'inzio
        build.setPrezzoTotale(prezzoFinale);
        
        build.getPeriferiche().add(periferica);
        //nuovo
        periferica.getBuildsPeriferiche().add(build);
        
        perifericaService.inserisci(periferica);
        model.addAttribute("build", build);
        model.addAttribute("ListaComponenti", this.componenteService.tutti());
        model.addAttribute("ListaPeriferiche", this.perifericaService.tutti());
        model.addAttribute("Componenti", build.getComponenti());
        model.addAttribute("Periferiche", build.getPeriferiche());
        return "build.html";
    }
    
    @Transactional
	@GetMapping("/admin/deletePeriferica/{id}")
	public String deleteDispaly(@PathVariable("id") Long id, Model model) {

		Periferica p = perifericaService.perifericaPerId(id);

		//TODO vedi se rimuovere
		// usato per eliminare l'ingrediente da ogni piatto in cui è presente
		for (BuildPC b : perifericaService.buildDiPeriferica(p)) {
			b.getPeriferiche().remove(p);
		}
	
		// cancellazione ingrediente
		perifericaService.deleteById(id);
		model.addAttribute("listaDisplay",this.perifericaService.displayPeriferiche());
		model.addAttribute("listaCuffie",this.perifericaService.cuffiePeriferiche());
		model.addAttribute("listaMouse",this.perifericaService.mousePeriferiche());
		model.addAttribute("listaTastiere",this.perifericaService.tastierePeriferiche());
		model.addAttribute("listaExtra",this.perifericaService.extraPeriferiche());
		
        return "admin/listaPeriferiche.html";
	}
    
  //NUOVO
  	//usato per rimuovere la componente dalla build
  	@Transactional
  	@GetMapping("/deletePerifericaDaBuild/{buildId}/{perifericaId}")
      public String deleteComponenteDaBuild(@PathVariable("buildId") Long buildId, @PathVariable("perifericaId") Long perifericaId, Model model) {
  		
  		Periferica p = perifericaService.perifericaPerId(perifericaId);
          BuildPC build = buildService.buildPerId(buildId);
          float prezzoFinale = build.getPrezzoTotale() - p.getPrezzo();
         
          build.setPrezzoTotale(prezzoFinale);
          
          //elimino il collegamento da build a componente e viceversa        
          build.getPeriferiche().remove(p);
          p.getBuildsPeriferiche().remove(build);
  		
          model.addAttribute("build", build);
          model.addAttribute("ListaComponenti", this.componenteService.tutti());
          model.addAttribute("ListaPeriferiche", this.perifericaService.tutti());
          model.addAttribute("Componenti", build.getComponenti());
          model.addAttribute("Periferiche", build.getPeriferiche());
          
          return "build.html";
  	}
    
}