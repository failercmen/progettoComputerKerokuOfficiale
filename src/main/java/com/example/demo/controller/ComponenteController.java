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

import com.example.demo.controller.validator.ComponenteValidator;
import com.example.demo.model.BuildPC;
import com.example.demo.model.Componente;
import com.example.demo.service.BuildPCService;
import com.example.demo.service.ComponenteService;
import com.example.demo.service.PerifericaService;

@Controller
public class ComponenteController {

	@Autowired
	private ComponenteService componenteService;

	@Autowired
	private ComponenteValidator componenteValidator;
	
	@Autowired
	private PerifericaService perifericaService;
	
	@Autowired
	private BuildPCService buildService;

	@RequestMapping(value = "/componenti", method = RequestMethod.GET)
	public String getListaComponenti(Model model) {
		model.addAttribute("listaCase", this.componenteService.caseComponenti());
		model.addAttribute("listaSchedeVideo", this.componenteService.schedaVideoComponenti());
		model.addAttribute("listaSchedeMadri", this.componenteService.schedaMadreComponenti());
		model.addAttribute("listaCpu", this.componenteService.cpuComponenti());
		model.addAttribute("listaRam", this.componenteService.ramComponenti());
		model.addAttribute("listaAlimentatori", this.componenteService.alimentatoreComponenti());
		model.addAttribute("listaCooling", this.componenteService.coolingComponenti());
		model.addAttribute("listaMemorie", this.componenteService.memorieComponenti());
		return "listaComponenti.html";
	}
	
	/*--------------------------------------------------------------------------*/
	
	@RequestMapping(value = "/admin/componenti", method = RequestMethod.GET)
	public String getListaComponentiAdmin(Model model) {
		
		model.addAttribute("listaCase", this.componenteService.caseComponenti());
		model.addAttribute("listaSchedeVideo", this.componenteService.schedaVideoComponenti());
		model.addAttribute("listaSchedeMadri", this.componenteService.schedaMadreComponenti());
		model.addAttribute("listaCpu", this.componenteService.cpuComponenti());
		model.addAttribute("listaRam", this.componenteService.ramComponenti());
		model.addAttribute("listaAlimentatori", this.componenteService.alimentatoreComponenti());
		model.addAttribute("listaCooling", this.componenteService.coolingComponenti());
		model.addAttribute("listaMemorie", this.componenteService.memorieComponenti());		
		return "admin/listaComponenti.html";
	}
	
	/*--------------------------------------------------------------------------*/

	@RequestMapping(value = "/admin/componente", method = RequestMethod.GET)
	public String addComponente(Model model) {
		model.addAttribute("componente", new Componente());
		return "admin/componenteForm.html";
	}

	@RequestMapping(value = "/admin/componente", method = RequestMethod.POST)
	public String addComponente(@ModelAttribute("componente") Componente componente, Model model,
			BindingResult bindingResult) {
		this.componenteValidator.validate(componente, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.componenteService.inserisci(componente);
//			model.addAttribute("listaComponenti", this.componenteService.tutti());
			model.addAttribute("listaCase", this.componenteService.caseComponenti());
			model.addAttribute("listaSchedeVideo", this.componenteService.schedaVideoComponenti());
			model.addAttribute("listaSchedeMadri", this.componenteService.schedaMadreComponenti());
			model.addAttribute("listaCpu", this.componenteService.cpuComponenti());
			model.addAttribute("listaRam", this.componenteService.ramComponenti());
			model.addAttribute("listaAlimentatori", this.componenteService.alimentatoreComponenti());
			model.addAttribute("listaCooling", this.componenteService.coolingComponenti());
			model.addAttribute("listaMemorie", this.componenteService.memorieComponenti());	
			return "/admin/listaComponenti.html";
		}
		return "/admin/componenteForm.html";
	}

	@RequestMapping(value = "/componente/{id}", method = RequestMethod.GET)
	public String getComponente(@PathVariable("id") Long id, Model model) {
		model.addAttribute("componente", this.componenteService.componentePerId(id));
		return "Componente.html";
	}
	
	@RequestMapping(value = "/admin/componente/{id}", method = RequestMethod.GET)
	public String getComponenteAdmin(@PathVariable("id") Long id, Model model) {
		model.addAttribute("componente", this.componenteService.componentePerId(id));
		return "admin/Componente.html";
	}
	
	@GetMapping("/buildComponente/{buildId}/{componenteId}")
    public String impostaComponente(@PathVariable("buildId") Long buildId, @PathVariable("componenteId") Long componenteId, Model model) {
        BuildPC build = buildService.buildPerId(buildId);
        Componente componente = componenteService.componentePerId(componenteId);
        
        //set prezzo
        Float prezzoFinale = build.getPrezzoTotale() + componente.getPrezzo(); //uguale a 0 all'inzio
        build.setPrezzoTotale(prezzoFinale);
        
        build.getComponenti().add(componente);
        //nuovo
        componente.getBuildsComponenti().add(build);
        
        
        componenteService.inserisci(componente);
        
        model.addAttribute("build", build);
        model.addAttribute("ListaComponenti", this.componenteService.tutti());
        model.addAttribute("ListaPeriferiche", this.perifericaService.tutti());
        model.addAttribute("Componenti", build.getComponenti());
        model.addAttribute("Periferiche", build.getPeriferiche());
        return "build.html";
    }
	

	@Transactional
	@GetMapping("/admin/deleteComponente/{id}")
	public String deleteComponente(@PathVariable("id") Long id, Model model) {

		Componente c = componenteService.componentePerId(id);

		//TODO vedi se rimuovere
		// usato per eliminare l'ingrediente da ogni piatto in cui è presente
		for (BuildPC b : componenteService.buildDiComponente(c)) {
			b.getComponenti().remove(c);
	}
	
		// cancellazione ingrediente
		componenteService.deleteById(id);
		model.addAttribute("listaCase", this.componenteService.caseComponenti());
		model.addAttribute("listaSchedeVideo", this.componenteService.schedaVideoComponenti());
		model.addAttribute("listaSchedeMadri", this.componenteService.schedaMadreComponenti());
		model.addAttribute("listaCpu", this.componenteService.cpuComponenti());
		model.addAttribute("listaRam", this.componenteService.ramComponenti());
		model.addAttribute("listaAlimentatori", this.componenteService.alimentatoreComponenti());
		model.addAttribute("listaCooling", this.componenteService.coolingComponenti());
		model.addAttribute("listaMemorie", this.componenteService.memorieComponenti());	
		
		return "admin/listaComponenti.html";
	}
	
	//NUOVO
	//usato per rimuovere la componente dalla build
	@Transactional
	@GetMapping("/deleteComponenteDaBuild/{buildId}/{componenteId}")
    public String deleteComponenteDaBuild(@PathVariable("buildId") Long buildId, @PathVariable("componenteId") Long componenteId, Model model) {
		
		Componente c = componenteService.componentePerId(componenteId);
        BuildPC build = buildService.buildPerId(buildId);
        float prezzoFinale = build.getPrezzoTotale() - c.getPrezzo();
        
        //elimino il collegamento da build a componente e viceversa
        build.setPrezzoTotale(prezzoFinale);
        build.getComponenti().remove(c);
        c.getBuildsComponenti().remove(build);
		
        model.addAttribute("build", build);
        model.addAttribute("ListaComponenti", this.componenteService.tutti());
        model.addAttribute("ListaPeriferiche", this.perifericaService.tutti());
        model.addAttribute("Componenti", build.getComponenti());
        model.addAttribute("Periferiche", build.getPeriferiche());
        
        return "build.html";
	}
}