package com.presta.customer.network

import com.presta.customer.organisation.OrganisationModel

object NetworkConstants {
    const val applicationsBaseUrl = "https://staging-lending.presta.co.ke/applications/api/v2/"
    const val guarantorBaseUrl = "https://eguarantorship-api.presta.co.ke/api/v1/"
    const val baseUrlKeycloak = "https://iam.presta.co.ke/auth/realms/"
    const val accountsUrlKeycloak = "https://accounts.presta.co.ke/users-admin/api/v1/"

    object PrestaAuthenticateClient {
        val route = baseUrlKeycloak + OrganisationModel.organisation.tenant_id + "/protocol/openid-connect/token"
    }

    object PrestaAuthenticateUser{
        const val route = applicationsBaseUrl + "authentication"
    }

    object PrestaCheckAuthUser {
        const val route = "https://accounts.presta.co.ke/authentication"
    }

    object PrestaOnBoardingClient {
        const val route = applicationsBaseUrl + "account-config"
    }

    object PrestaUpdatePinTermsClient {
        const val route = applicationsBaseUrl + "pin-and-terms-acceptance"
    }

    object PrestaSelfRegistration {
        const val route = applicationsBaseUrl + "customers/self-registration"
    }

    object PrestaOtpRequestClient {
        const val route = applicationsBaseUrl + "otp"
    }

    object PrestaOtpVerifyClient {
        const val route = applicationsBaseUrl + "otp/validation"
    }

    object PrestaCheckPinClient {
        const val route = accountsUrlKeycloak + "auth/ussd/has-pin"
    }
}