package org.restcomm.connect.rvd.interpreter.serialization;

import com.thoughtworks.xstream.XStream;
import org.restcomm.connect.rvd.model.rcml.RcmlResponse;
import org.restcomm.connect.rvd.model.steps.dial.ClientNounConverter;
import org.restcomm.connect.rvd.model.steps.dial.ConferenceNounConverter;
import org.restcomm.connect.rvd.model.steps.dial.NumberNounConverter;
import org.restcomm.connect.rvd.model.steps.dial.RcmlClientNoun;
import org.restcomm.connect.rvd.model.steps.dial.RcmlConferenceNoun;
import org.restcomm.connect.rvd.model.steps.dial.RcmlDialStep;
import org.restcomm.connect.rvd.model.steps.dial.RcmlNumberNoun;
import org.restcomm.connect.rvd.model.steps.dial.RcmlSipuriNoun;
import org.restcomm.connect.rvd.model.steps.dial.SipuriNounConverter;
import org.restcomm.connect.rvd.model.steps.email.EmailStepConverter;
import org.restcomm.connect.rvd.model.steps.email.RcmlEmailStep;
import org.restcomm.connect.rvd.model.steps.fax.FaxStepConverter;
import org.restcomm.connect.rvd.model.steps.fax.RcmlFaxStep;
import org.restcomm.connect.rvd.model.steps.gather.RcmlGatherStep;
import org.restcomm.connect.rvd.model.steps.hangup.RcmlHungupStep;
import org.restcomm.connect.rvd.model.steps.pause.RcmlPauseStep;
import org.restcomm.connect.rvd.model.steps.play.PlayStepConverter;
import org.restcomm.connect.rvd.model.steps.play.RcmlPlayStep;
import org.restcomm.connect.rvd.model.steps.record.RcmlRecordStep;
import org.restcomm.connect.rvd.model.steps.redirect.RcmlRedirectStep;
import org.restcomm.connect.rvd.model.steps.redirect.RedirectStepConverter;
import org.restcomm.connect.rvd.model.steps.reject.RcmlRejectStep;
import org.restcomm.connect.rvd.model.steps.say.RcmlSayStep;
import org.restcomm.connect.rvd.model.steps.say.SayStepConverter;
import org.restcomm.connect.rvd.model.steps.sms.RcmlSmsStep;
import org.restcomm.connect.rvd.model.steps.sms.SmsStepConverter;
import org.restcomm.connect.rvd.model.steps.ussdcollect.UssdCollectRcml;
import org.restcomm.connect.rvd.model.steps.ussdlanguage.UssdLanguageConverter;
import org.restcomm.connect.rvd.model.steps.ussdlanguage.UssdLanguageRcml;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayRcml;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayStepConverter;

/**
 * Serializes Rcml*Step to RCML code
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RcmlSeriallizer {

    private XStream xstream;

    public RcmlSeriallizer() {
        xstream = new XStream();
        xstream.registerConverter(new SayStepConverter());
        xstream.registerConverter(new PlayStepConverter());
        xstream.registerConverter(new RedirectStepConverter());
        xstream.registerConverter(new SmsStepConverter());
        xstream.registerConverter(new FaxStepConverter());
        xstream.registerConverter(new EmailStepConverter());
        xstream.registerConverter(new NumberNounConverter());
        xstream.registerConverter(new ClientNounConverter());
        xstream.registerConverter(new ConferenceNounConverter());
        xstream.registerConverter(new SipuriNounConverter());
        xstream.registerConverter(new UssdSayStepConverter());
        xstream.registerConverter(new UssdLanguageConverter());
        xstream.addImplicitCollection(RcmlDialStep.class, "nouns");
        xstream.alias("Response", RcmlResponse.class);
        xstream.addImplicitCollection(RcmlResponse.class, "steps");
        xstream.alias("Say", RcmlSayStep.class);
        xstream.alias("Play", RcmlPlayStep.class);
        xstream.alias("Gather", RcmlGatherStep.class);
        xstream.alias("Dial", RcmlDialStep.class);
        xstream.alias("Hangup", RcmlHungupStep.class);
        xstream.alias("Redirect", RcmlRedirectStep.class);
        xstream.alias("Reject", RcmlRejectStep.class);
        xstream.alias("Pause", RcmlPauseStep.class);
        xstream.alias("Sms", RcmlSmsStep.class);
        xstream.alias("Email", RcmlEmailStep.class);
        xstream.alias("Record", RcmlRecordStep.class);
        xstream.alias("Fax", RcmlFaxStep.class);
        xstream.alias("Number", RcmlNumberNoun.class);
        xstream.alias("Client", RcmlClientNoun.class);
        xstream.alias("Conference", RcmlConferenceNoun.class);
        xstream.alias("Sip", RcmlSipuriNoun.class);
        xstream.alias("UssdMessage", UssdSayRcml.class);
        xstream.alias("UssdCollect", UssdCollectRcml.class);
        xstream.alias("Language", UssdLanguageRcml.class);
        xstream.addImplicitCollection(RcmlGatherStep.class, "steps");
        xstream.addImplicitCollection(UssdCollectRcml.class, "messages");
        xstream.useAttributeFor(UssdCollectRcml.class, "action");
        xstream.useAttributeFor(RcmlGatherStep.class, "action");
        xstream.useAttributeFor(RcmlGatherStep.class, "timeout");
        xstream.useAttributeFor(RcmlGatherStep.class, "finishOnKey");
        xstream.useAttributeFor(RcmlGatherStep.class, "method");
        xstream.useAttributeFor(RcmlGatherStep.class, "numDigits");
        xstream.useAttributeFor(RcmlSayStep.class, "voice");
        xstream.useAttributeFor(RcmlSayStep.class, "language");
        xstream.useAttributeFor(RcmlSayStep.class, "loop");
        xstream.useAttributeFor(RcmlPlayStep.class, "loop");
        xstream.useAttributeFor(RcmlRejectStep.class, "reason");
        xstream.useAttributeFor(RcmlPauseStep.class, "length");
        xstream.useAttributeFor(RcmlRecordStep.class, "action");
        xstream.useAttributeFor(RcmlRecordStep.class, "method");
        xstream.useAttributeFor(RcmlRecordStep.class, "timeout");
        xstream.useAttributeFor(RcmlRecordStep.class, "finishOnKey");
        xstream.useAttributeFor(RcmlRecordStep.class, "maxLength");
        xstream.useAttributeFor(RcmlRecordStep.class, "transcribe");
        xstream.useAttributeFor(RcmlRecordStep.class, "transcribeCallback");
        xstream.useAttributeFor(RcmlRecordStep.class, "playBeep");
        xstream.useAttributeFor(RcmlRecordStep.class, "media");
        xstream.useAttributeFor(RcmlDialStep.class, "action");
        xstream.useAttributeFor(RcmlDialStep.class, "method");
        xstream.useAttributeFor(RcmlDialStep.class, "timeout");
        xstream.useAttributeFor(RcmlDialStep.class, "timeLimit");
        xstream.useAttributeFor(RcmlDialStep.class, "callerId");
        xstream.useAttributeFor(RcmlDialStep.class, "record");
        xstream.aliasField("Number", RcmlDialStep.class, "number");
        xstream.aliasField("Client", RcmlDialStep.class, "client");
        xstream.aliasField("Conference", RcmlDialStep.class, "conference");
        xstream.aliasField("Uri", RcmlDialStep.class, "sipuri");
    }

    public String serialize(RcmlResponse rcmlResponse) {
        return xstream.toXML(rcmlResponse);
    }
}
