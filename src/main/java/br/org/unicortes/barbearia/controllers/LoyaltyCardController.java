package br.org.unicortes.barbearia.controllers;

import br.org.unicortes.barbearia.dtos.LoyaltyCardDTO;
import br.org.unicortes.barbearia.models.Client;
import br.org.unicortes.barbearia.models.LoyaltyCard;
import br.org.unicortes.barbearia.models.Servico;
import br.org.unicortes.barbearia.services.LoyaltyCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/loyalty-cards")
public class LoyaltyCardController {

    @Autowired
    private LoyaltyCardService loyaltyCardService;

    @GetMapping
    public ResponseEntity<List<LoyaltyCardDTO>> getAllLoyaltyCards() {
        List<LoyaltyCardDTO> loyaltyCards = loyaltyCardService.getAllLoyaltyCards().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loyaltyCards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyCardDTO> getLoyaltyCardById(@PathVariable Long id) {
        LoyaltyCard loyaltyCard = loyaltyCardService.getLoyaltyCardById(id);
        return ResponseEntity.ok(convertToDTO(loyaltyCard));
    }

    @PostMapping
    public ResponseEntity<LoyaltyCardDTO> createLoyaltyCard(@RequestBody LoyaltyCardDTO loyaltyCardDTO) {
        LoyaltyCard loyaltyCard = convertToEntity(loyaltyCardDTO);
        LoyaltyCard newLoyaltyCard = loyaltyCardService.createLoyaltyCard(loyaltyCard);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(newLoyaltyCard));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoyaltyCardDTO> updateLoyaltyCard(@PathVariable Long id, @RequestBody LoyaltyCardDTO loyaltyCardDTO) {
        LoyaltyCard loyaltyCard = convertToEntity(loyaltyCardDTO);
        LoyaltyCard updatedLoyaltyCard = loyaltyCardService.updateLoyaltyCard(id, loyaltyCard);
        return ResponseEntity.ok(convertToDTO(updatedLoyaltyCard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoyaltyCard(@PathVariable Long id) {
        loyaltyCardService.deleteLoyaltyCard(id);
        return ResponseEntity.noContent().build();
    }

    private LoyaltyCardDTO convertToDTO(LoyaltyCard loyaltyCard) {
        LoyaltyCardDTO loyaltyCardDTO = new LoyaltyCardDTO();
        loyaltyCardDTO.setId(loyaltyCard.getId());
        loyaltyCardDTO.setClientId(loyaltyCard.getClient().getId());
        loyaltyCardDTO.setDateAdmission(loyaltyCard.getDateAdmission());
        loyaltyCardDTO.setServiceId(loyaltyCard.getService().getId());
        loyaltyCardDTO.setPoints(loyaltyCard.getPoints());
        return loyaltyCardDTO;
    }

    private LoyaltyCard convertToEntity(LoyaltyCardDTO loyaltyCardDTO) {
        LoyaltyCard loyaltyCard = new LoyaltyCard();
        loyaltyCard.setId(loyaltyCardDTO.getId());
        // Assume que os IDs são usados para recuperar Client e Servico se necessário
        // É importante garantir que Client e Servico existam antes de atribuir, ou use IDs diretamente
        Client client = new Client();
        client.setId(loyaltyCardDTO.getClientId());
        loyaltyCard.setClient(client);

        Servico service = new Servico();
        service.setId(loyaltyCardDTO.getServiceId());
        loyaltyCard.setService(service);

        loyaltyCard.setDateAdmission(loyaltyCardDTO.getDateAdmission());
        loyaltyCard.setPoints(loyaltyCardDTO.getPoints());
        return loyaltyCard;
    }
}
