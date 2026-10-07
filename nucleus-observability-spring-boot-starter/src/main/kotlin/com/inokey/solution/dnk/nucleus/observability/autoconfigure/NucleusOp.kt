package com.inokey.solution.dnk.nucleus.observability.autoconfigure

/**
 * Déclare la politique technique d'une opération publique MultiPlanner.
 *
 * Une seule annotation matérialise :
 * - l'opération observable ;
 * - la politique de corrélation ;
 * - la politique d'idempotence.
 *
 * La syntaxe historique `@NucleusOp(MultiplannerOperation.X)` reste valide
 * et conserve un comportement non bloquant hors contexte HTTP. Les routes HTTP
 * doivent déclarer explicitement leur politique de corrélation.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NucleusOp(
    val value: MultiplannerOperation,
    val correlation: NucleusCorrelationMode = NucleusCorrelationMode.NONE,
    val idempotency: NucleusIdempotencyMode = NucleusIdempotencyMode.NONE,
    val extraTags: Array<String> = [],
)
