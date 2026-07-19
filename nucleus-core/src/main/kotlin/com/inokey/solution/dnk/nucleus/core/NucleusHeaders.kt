package com.inokey.solution.dnk.nucleus.core

import com.inokey.solution.dnk.nucleus.enum.ConstantHeader

/**
 * Constantes de headers HTTP propagés par Nucleus.
 * Stables — jamais modifiées par les applications consommatrices.
 *
 * Délègue vers [ConstantHeader] qui est la source canonique unique.
 */
object NucleusHeaders {
    val CORRELATION_ID: String = ConstantHeader.CORRELATION_ID
    val SESSION_ID: String     = ConstantHeader.SESSION_ID
    val CONSENT_VERSION: String = ConstantHeader.CONSENT_VERSION
    val IDEMPOTENCY_KEY: String = ConstantHeader.IDEMPOTENCY_KEY
    val REQUEST_TIMING: String  = ConstantHeader.REQUEST_TIMING
}

