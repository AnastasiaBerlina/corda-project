package com.template.webserver;

import com.template.flows.Initiator;
import com.template.states.GradeState;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/grade", produces = "text/plain")
    private String templateendpoint(@RequestParam String lectorName,
                                    @RequestParam String studentName,
                                    @RequestParam String subjectName,
                                    @RequestParam long grade) throws ExecutionException, InterruptedException {
        Party professor = proxy.partiesFromName(lectorName, false).iterator().next();
        Party student = proxy.partiesFromName(studentName, false).iterator().next();
        SignedTransaction signedTransaction = proxy.startFlowDynamic(Initiator.class,
                new GradeState(professor, student, grade, subjectName)).getReturnValue().get();
        return signedTransaction.getId().toString();

    }
}