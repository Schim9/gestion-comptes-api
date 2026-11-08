package com.famille.gestioncompte.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOperationRequest(
    @NotBlank String libelle,
    String date,       // numéro du jour, null pour opérations à venir
    Double montant,    // signé (négatif = débit)
    @NotBlank String compte      // "CCM" ou "Boursorama"
) {}
