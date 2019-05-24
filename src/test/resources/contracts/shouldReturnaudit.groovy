package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description "Should return audits"
    request{
        method GET()
        url("/audits")
    }
    response {
        status 200
        body("""
            {
              "audits": []
            }
            """)
    }
}
