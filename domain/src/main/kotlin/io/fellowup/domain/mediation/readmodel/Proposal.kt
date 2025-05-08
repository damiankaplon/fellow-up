package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.matchmaking.Location

interface Proposal {
    val acceptedBy: Int
    val location: Location
}