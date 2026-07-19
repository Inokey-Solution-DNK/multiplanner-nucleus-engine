package com.inokey.solution.dnk.nucleus.annotation

/**
 * @deprecated Use [com.inokey.solution.dnk.nucleus.observability.autoconfigure.NucleusOp]
 * with `MultiplannerOperation` enum instead. This annotation is kept for backward
 * compatibility but is not used by the observability aspect.
 */
@Deprecated("Use observability NucleusOp with MultiplannerOperation enum", level = DeprecationLevel.WARNING)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class NucleusOp(
    val code: String,
    val surface: String = ""
)

