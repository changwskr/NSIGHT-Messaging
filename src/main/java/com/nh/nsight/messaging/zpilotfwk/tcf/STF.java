package com.nh.nsight.messaging.zpilotfwk.tcf;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.Config;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.Constants;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.ICommonManagementSB;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.Reflector;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.SessionContext;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TCFConstants;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TPMSVCAPI;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TPSVCINFODTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TransactionControlDAO;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.UserTransaction;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.XMLCache;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.ZpilotFwkContext;

public class STF {
  private static STF instance;
  private String transaction_type = "container";
  private EPlatonEvent eplevent;
  private EPlatonCommonDTO commonDTO;
  private TPSVCINFODTO tpsvcinfoDTO;
  private String bankCode;
  private String branchCode;
  private String channelType;
  private String businessDate;
  private String eventNo;
  private String transactionNo;
  private String baseCurrency;
  /** Spring Bean — STF_SPinit()에서 ZpilotFwkContext로 조회 */
  private ICommonManagementSB commonManagementSB;
  /** usertransaction 모드에서 사용하는 사용자 트랜잭션 */
  private UserTransaction tx;
  private SessionContext ctx;
  private int giTXInfoflag = 0;
  public static HashMap hm_system_log_level;

  public STF() {
  }

  public STF(String transaction_type, SessionContext ctx) {
    this.transaction_type = transaction_type;
    this.ctx = ctx;
  }

  public STF(UserTransaction tx, SessionContext ctx) {
    this.tx = tx;
    this.ctx = ctx;
  }

  private static LOGEJ log() {
    return LOGEJ.getInstance();
  }

  /** TCF 연동용 — container / usertransaction 모드에 맞는 STF 생성 */
  public static STF create(String transactionType, SessionContext sessionContext) {
    return new STF(transactionType, sessionContext);
  }

  /**
   * STF 싱글톤 인스턴스 (레거시).
   */
  public static synchronized STF getInstance() {
    if (instance == null) {
      try {
        instance = new STF();
      } catch (Exception igex) {
        // ignore
      }
    }
    return instance;
  }

  /**
   * EPlatonEvent 객체를 반환하는 함수
   * 
   * @return
   */
  public EPlatonEvent getEPlatonEvent() {
    return eplevent;
  }

  /**
   * 실행모듈
   * 
   * @param pevent
   * @return
   */
  public EPlatonEvent execute(EPlatonEvent pevent) {

    try {
      eplevent = pevent;

      log().printf(4, pevent, "==================[STF_SPinit] start");
      STF_SPinit();

      char errorFlag = errorCodeFlag(tpsvcinfoDTO);
      if (errorFlag == 'E') {
        log().printf(10, pevent, "STF_SPinit error");
        log().printf(10, pevent, "==================[STF_SPinit] end");
        log().printf(10, pevent, "==================[STF_SPend] start");
        STF_SPend();
        log().printf(10, pevent, "==================[STF_SPend] end");
        return eplevent;
      }
      if (errorFlag == 'I') {
        log().printf(4, pevent, "STF_SPinit success");
        log().printf(4, pevent, "==================[STF_SPinit] end");
        log().printf(4, pevent, "==================[STF_SPmiddle] start");
        STF_SPmiddle();
        log().printf(4, pevent, "==================[STF_SPmiddle] end");
      }
      log().printf(4, pevent, "==================[STF_SPend] start");
      STF_SPend();
      log().printf(4, pevent, "==================[STF_SPend] end");
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0001", this.getClass().getName() + ".execute():" + ex.toString());
      log().eprintf(10, pevent, ex);
      log().printf(10, pevent, "STF.execute:EXCEPTION:" + ex.toString());
    }

    return eplevent;
  }

  private static char errorCodeFlag(TPSVCINFODTO dto) {
    if (dto == null || dto.getErrorcode() == null || dto.getErrorcode().isEmpty()) {
      return 'E';
    }
    return dto.getErrorcode().charAt(0);
  }

  /**
   * 기본적인 정보를 셋팅하는 함수
   * 에러코드를 IZZ000으로 맞춘다
   * 트랜잭션의 시작을 알리기위한 플래그를 셋팅한다
   * 시스템일자를 셋팅한다
   * 시스템시간을 셋팅한다
   * 영업일자를 셋팅한다.
   * 통화코드를 셋팅한다.
   * 
   * @return
   */
  public boolean STF_SPinit() {
    try {

      commonDTO = (EPlatonCommonDTO) eplevent.getCommon();
      tpsvcinfoDTO = eplevent.getTPSVCINFODTO();
      bankCode = commonDTO.getBankCode();
      branchCode = commonDTO.getBranchCode();
      channelType = commonDTO.getChannelType();
      businessDate = commonDTO.getBusinessDate();
      eventNo = commonDTO.getEventNo();
      commonManagementSB = ZpilotFwkContext.getBean(ICommonManagementSB.class);

      /*************************************************************************
       * 에러코드정보를 초기화한다.
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "ErrorCode set :: IZZ000");
      tpsvcinfoDTO.setErrorcode("IZZ000");

      /*************************************************************************
       * 트랜잭션의 시작정보를 알기위한 FLAG SET.
       * 1 - already transaction start
       * 0 - transaction not start
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "gitxInfoflag set :: 0");
      giTXInfoflag = 0;

      /*************************************************************************
       * 시스탬날짜을 설정한다.
       ************************************************************************/
      /*
       * TimeProcess timeProcess = TimeProcess.getInstance();
       * String systemDateAndTime =
       * timeProcess.getSystemDate(commonDTO.getTimeZone());
       * commonDTO.setSystemDate(systemDateAndTime.substring(0, 8));
       */
      String systemDateAndTime = CommonUtil.GetSysDate();
      commonDTO.setSystemDate(systemDateAndTime.substring(0, 8));
      log().printf(4, (EPlatonEvent) eplevent, "System Date set :: " + systemDateAndTime.substring(0, 8));

      /*************************************************************************
       * 시스템시간을 설정한다. (HHmmssSS — 8자리)
       ************************************************************************/
      String systemInTime = CommonUtil.GetSysTime();
      commonDTO.setSystemInTime(systemInTime);
      log().printf(4, eplevent, "System Time set :: " + systemInTime);

      /*************************************************************************
       * Base Currency
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Base Currency set :: 11 ");
      baseCurrency = commonManagementSB.getBaseCurrency("11");
      commonDTO.setBaseCurrency(baseCurrency);

      /*************************************************************************
       * 영업일자을 설정한다.
       ************************************************************************/
     

      if ((businessDate = commonManagementSB.getBusinessDate("11")) == null) {
        STF_SPerror("EFWK0005", this.getClass().getName() + ".STF_SPinit()::get business date error");
        log().printf(10, (EPlatonEvent) eplevent, "STF_SPinit()::get business date error");
      } else
        commonDTO.setBusinessDate(businessDate);

      log().printf(4, (EPlatonEvent) eplevent, "Business Date :: " + businessDate);

      return true;
    } catch (Exception ex) {
      STF_SPerror("EFWK0002", this.getClass().getName() + ".STF_SPinit():" + ex.toString());
      log().eprintf(10, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  /**
   * 현재 클라이언트에서나 서버에서의 트랜잭션의 시작정보를 관리한다.
   * 만약 트랜잭션을 시작 했다면 기존 트랜잭션에 합류시키며 그렇지 않은 경우에는
   * 트랜잭션을 새로이 시작한다.
   *
   *
   * @return
   */
  public boolean STF_SPmiddle() {
    try {

      if (!STF_SPmiddleTransaction()) {
        return false;
      }

      /*************************************************************************
       * 전체 트랜잭션 번호 관리
       * ***********************************************************************
       * SEQUENCE를 채번한후 거래별 트랜잭션 번호를 셋팅한다.
       * 이 작업은 로깅작업을 최초 TPSrecv()시에 트랜잭션에 대한 번호를 채번하는 것으로 한다.
       * 참고사항
       * 향후 트랜잭션에 채번은 TPSrecv() TPM 모듈에서 실시한다
       * 이는 로깅관련 작업을 위해..
       * 이로직은 전체 트랜잭션번호외의 타 레퍼런스를 구하기 위한 것으로 바꾼다.
       * 단지 여기서는 TPSrecv모듈에서 구한 시퀀스를 확인하고 TPSVCINFODTO 객체 셋팅하는 것
       * 으로 한다.
       ************************************************************************/
      if (!"100".equals(tpfq()))
        tpsvcinfoDTO.setOrgseq(tpsvcinfoDTO.getHostseq());
      commonDTO.setTransactionNo(tpsvcinfoDTO.getHostseq());
      log().printf(4, (EPlatonEvent) eplevent,
          "HOSTSEQ:" + tpsvcinfoDTO.getHostseq() + " ORGSEQ:" + tpsvcinfoDTO.getOrgseq());

      if (isErr()) {
        return false;
      }

      /*************************************************************************
       * 마감전후 구분 필드를 세운다.
       *************************************************************************
       * = 시간상 관리
       * 자동화기기 마감전후를 구분하여 마감구분 필드를 세운다.
       * 토요일 : 13시 * 평 일 : 16시30
       * 향후 이 로직을 위한 추가 로직을 구성한다.
       *************************************************************************
       * = 거래별 제어관리
       * 시스템파라미터의 상태코드가 10인경우 : 모든 트랜잭션을 허용
       * 시스템파라미터의 상태코드가 20인경우 : 모든 트랜잭션을 금지
       * 시스템파라미터의 상태코드가 50인경우 : ATM/IBANK 관련된 거래만 허용
       *
       ***********************************************************************/
      log().print(1, (EPlatonEvent) eplevent, "Set EOD Field ::");
      STF_SPeod();
      log().printf(4, (EPlatonEvent) eplevent, "[" + tpsvcinfoDTO.getTrclass() + "]");
      if (isErr()) {
        return false;
      }

      /*************************************************************************
       * 거래제어정보를 관리한다
       *************************************************************************
       * 뱅크코드별 거래 제어
       * 브렌치별 거래 제어
       * 거래코드별 거래제어
       * 텔러별 거래 제어
       * 온라인 거래 제어
       * 배치 거래 제어
       * 온라인 거래만 허용
       * 배치 거래만 허용
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Set Control Transaction ::");
      // STF_SPtxctl();
      STF_SPtxblocking();
      if (isErr()) {
        return false;
      }

      /*************************************************************************
       * 웹컴포넌단 - EJB 서버단과의 TIMEOUT 관리
       *************************************************************************
       * 이 모듈은 TPSVCINFO의 TXTIMER을 기준으로서 관리한다
       * 즉 웹단에서 시작시간을 기초로 해서 현재시간을 기초로 해서 INTERVAL이 넘어서면
       * 다음 업무단의 로직을 처리하지 않고 바로 에러로 처리
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Check Transaction Timeout between WAF and EJB Tier ::");
      STF_SPwebtxtimer();
      if (isErr()) {
        log().printf(4, (EPlatonEvent) eplevent, "error");
        return false;
      }

      /*************************************************************************
       * 클라이언트의 호출정보를 보여준다. (TPFQ)
       *************************************************************************
       * 200 - 온라인 클라이언트
       * 100 - 온라인 서버에서 호출
       * 300 - 대외기관에서 호출
       * 400 - 배치에서 호출
       * 800 - 인터넷뱅킹에서 호출
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Check Call Transaction Location :: ");
      switch (CommonUtil.Str2Int(tpsvcinfoDTO.getTpfq())) {
        case 100:
          log().printf(4, (EPlatonEvent) eplevent, "TPFQ : 100 ");
          break;
        case 200:
          log().printf(4, (EPlatonEvent) eplevent, "TPFQ : 200 ");
          break;
        case 300:
          log().printf(4, (EPlatonEvent) eplevent, "TPFQ : 300 ");
          break;
        case 400:
          log().printf(4, (EPlatonEvent) eplevent, "TPFQ : 400 ");
          break;
        default:
          log().printf(10, (EPlatonEvent) eplevent, "TPFQ : not valid ");
          STF_SPerror("EFWK0007", this.getClass().getName() + ".STF_SPmiddle():TPFQ NOT VALID");
          break;
      }
      log().printf(4, (EPlatonEvent) eplevent, "클라이언트의 호출위치 정보를 가지고 온다 : [" + tpsvcinfoDTO.getTpfq() + "]");
      if (isErr()) {
        return false;
      }

      /*************************************************************************
       * TPMSVCINFO 정보를 셋팅한다.
       *************************************************************************
       * call_service_name
       * call_tpm_in_time
       * call_tpm_out_time
       * call_tpme_interval
       * call_tpm_stf_in_time
       * call_tpm_stf_out_time
       * call_tpm_etf_in_time
       * call_tpm_etf_out_time
       * call_tpme_service_interval
       * error_code
       * call_hostseq
       * call_orgseq
       * call_location
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Set TPMSVCINFO ::");
      STF_SPsettpmsvcinfo();

      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0003", this.getClass().getName() + ".STF_SPmiddle():" + ex.toString());
      log().printf(10, (EPlatonEvent) eplevent, ".STF_SPmiddle():" + ex.toString());
      log().eprintf(10, (EPlatonEvent) eplevent, ex);

      return false;
    }
  }

  public boolean STF_SPend() {
    try {

      /*************************************************************************
       * 기본정보를 재셋팅한다.
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Set SystemOuttime ::");
      commonDTO.setSystemOutTime(CommonUtil.GetSysTime());

      /*************************************************************************
       * TPMSVCINFO 정보를 재셋팅한다.
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Set TPMSVCINFO ::");
      tpsvcinfoDTO.setSystemInTime(commonDTO.getSystemInTime());

      /*************************************************************************
       * 패킷정보를 재셋팅한다.
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "Move Packet Rebuild ::");
      STF_SPmovepacket();

      /*************************************************************************
       * 공통정보 로깅작을 실시한다.
       ************************************************************************/
      /***************************************************************************
       * 트랜잭션에대한 로깅정보 데이타베이스에 저장한다.
       * 데이타베이스에 남기는 INPUT/OUTPUT 정보는 에러인경우에는 남기는 것이 어렵다.
       * 왜냐하면 USERTRANSACTION,CONTAINER 두가지 타입에 있어서 rollback()을 하는것이
       * 기본이므로 만약 에러발생시 여기서 DB 작업시 예외를 만들어 낼것이다.
       * 그러므로 에러가 발생시는 입력/출력 패킷을 남기지 않고 대신 파일로 남겨주는 것으로
       * 한다.
       **************************************************************************/
      if (this.eplevent.getTPSVCINFODTO().getErrorcode().charAt(0) != 'E') {

        log().printf(4, (EPlatonEvent) eplevent, "==================[STF_SPdbInLog() START]");
        if (!STF_SPdbInLog(eplevent)) {
          log().printf(4, (EPlatonEvent) eplevent, "STF_SPdbInLog() error");
        } else {
          log().printf(4, (EPlatonEvent) eplevent, "STF_SPdbInLog() success");
        }
        log().printf(4, (EPlatonEvent) eplevent, "==================[STF_SPdbInLog() END] (true)");

      }

      /*************************************************************************
       * 공통정보 로깅작을 실시한다.
       ************************************************************************/
      log().printf(4, (EPlatonEvent) eplevent, "==================[STF_SPcommonLog START]");
      if (!STF_SPcommonLog()) {
        log().printf(10, (EPlatonEvent) eplevent, "STF_SPcommonLog() error");
      } else {
        log().printf(4, (EPlatonEvent) eplevent, "STF_SPcommonLog() success");
      }
      log().printf(4, (EPlatonEvent) eplevent, "==================[STF_SPcommonLog() END] (true)");

      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0004", this.getClass().getName() + ".STF_SPend():" + ex.toString());
      log().eprintf(10, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  private boolean isErr() {
    switch (tpsvcinfoDTO.getErrorcode().charAt(0)) {
      case 'e':
      case 's':
      case 'E':
      case 'S':
        return true;
      case 'I':
        return false;
      case '*':
      default:
        STF_SPerror("EBD800", "에러코드가 셋팅안됨");
        return true;
    }
  }

  /**
   * TRANSACTION_UPDOWN 테이블에 INPUT 패킷 로깅.
   */
  private boolean STF_SPdbInLog(EPlatonEvent event) {
    try {
      TransactionControlDAO dao = ZpilotFwkContext.getBean(TransactionControlDAO.class);
      if (!dao.DB_INSERTinlog(event)) {
        STF_SPerror("EFWK0008", this.getClass().getName() + ".STF_SPdbInLog():INPUT PACKET EXCEPTION");
        return false;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0009", this.getClass().getName() + ".STF_SPdbInLog():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return false;
    }
    return true;
  }

  private boolean STF_SPcommonLog() {
    String LOGFILENAME = null;
    FileOutputStream fos = null;
    PrintStream ps = null;

    try {
      /*************************************************************************
       * 트랜잭션의 OUTTIME을 다시 초기화한다.
       ************************************************************************/
      LOGFILENAME = TCFConstants.INPUT_LOGFILENAME + CommonUtil.GetHostName() + "." +
          eplevent.getTPSVCINFODTO().getSystem_name() + "." +
          "in" + "." +
          CommonUtil.GetSysDate();
      fos = new FileOutputStream(LOGFILENAME, true);
      ps = new PrintStream(fos);

      ps.println(eplevent.getTPSVCINFODTO().getOperation_name() + "|" + eplevent.getTPSVCINFODTO().getOrgseq() + "-"
          + Reflector.objectToString(eplevent));

      ps.flush();
      ps.close();
      fos.close();

    } catch (Exception e) {
      try {
        if (fos != null)
          fos.close();
        if (ps != null)
          ps.close();
      } catch (Exception ex) {
      }
      e.printStackTrace();
      STF_SPerror("EFWK0010", this.getClass().getName() + ".STF_SPcommonLog():" + e.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, e);
      return false;
    }
    return true;
  }

  /*************************************************************************
   * 거래제어정보를 관리한다
   *************************************************************************
   * 뱅크코드별 거래 제어
   * 브렌치별 거래 제어
   * 거래코드별 거래제어
   * 텔러별 거래 제어
   * 온라인 거래 제어
   * 배치 거래 제어
   * 온라인 거래만 허용
   * 배치 거래만 허용
   ************************************************************************/
  private int STF_SPtxctl() {
    /**********************************************************************
     * 일단 데이타베이스의 테이블로 관리 할 것이다
     * 거래코드별, 단말별, 온라인/배치, 텔러별 나누어서
     * 트랜잭션 제어정보를 관리 할 것임
     **********************************************************************/
    try {

      /*
       * 모든 온라인거래에 대한 제어 여부를 판별한다.
       */
      log().printf(4, (EPlatonEvent) eplevent, "모든 온라인거래에 대한 제어 여부를 판별한다");

      /*
       * 모든 배치거래에 대한 제어 여부를 판별한다
       */
      log().printf(4, (EPlatonEvent) eplevent, "모든 배치거래에 대한 제어 여부를 판별한다");

      /*
       * ATM 거래에 대한 제어여부를 관리한다
       */
      log().printf(4, (EPlatonEvent) eplevent, "ATM 거래에 대한 제어여부를 관리한다");

      /*
       * 인터넷뱅 거래에 대한 제어여부를 관리한다
       */
      log().printf(4, (EPlatonEvent) eplevent, "인터넷뱅 거래에 대한 제어여부를 관리한다");

      /*
       * 거래코드에 대한 제어여부를 관리한다
       * 10개의 거래코드 버럭을 지정
       * 1000 - 1002
       * 1010 - 1023
       * 1099 - 1100
       * ....
       */
      log().printf(4, (EPlatonEvent) eplevent, "거래코드에 대한 제어여부를 관리한다");

      /*
       * 뱅크코드에 대한 텔러별 제어 정보를 관리한다
       */

      log().printf(4, (EPlatonEvent) eplevent, "뱅크코드에 대한 텔러별 제어 정보를 관리한다");

      return 0;
    } catch (Exception ex) {
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPtxctl():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return -1;
    }
  }

  private void STF_SPtxblocking() {
    try {
      if (STF_SPbankcodeblock())
        return;
      if (STF_SPtxcodeblock())
        return;
      if (STF_SPsystemblock())
        return;
      if (STF_SPtpfqblock())
        return;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPtxctl():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
    }
  }

  private boolean STF_SPtxcodeblock() {
    String configFileName = null;
    String block_txcode_tag = "block-txcode";
    String count_tag = "count";
    String mode_tag = "mode";
    String start_tag = "s";
    String mode_value = null;
    String count_value = null;
    int icount_value = 0;

    try {
      int ctxcode = CommonUtil.atoi(commonDTO.getEventNo());
      configFileName = Config.getInstance().getElement(Constants.BIZDELEGATE_TAG).getTextTrim();
      mode_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(mode_tag);
      count_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(count_tag);
      if (mode_value != null
          && count_value != null
          && mode_value.equals("on")
          && CommonUtil.CHECKisdigit(count_value, count_value.length())) {
        for (int i = 1; i <= CommonUtil.atoi(count_value); i++) {
          String tag = start_tag + 1;
          String val = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
              .getChildTextTrim(tag);
          int sv = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 1, "-"));
          int ev = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 2, "-"));
          if (ctxcode >= sv && ctxcode <= ev) {
            STF_SPerror("EFWK0011", "STF_SPtxcodeblock():해당거래코드는 거래안됨");
            log().printf(10, this.eplevent, "Transaction Block -- txcode ");
            return true;
          }
        }
      }
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPtxctl():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  private boolean STF_SPsystemblock() {
    String configFileName = null;
    String block_txcode_tag = "block-system";
    String count_tag = "count";
    String mode_tag = "mode";
    String bank_tag = "bank";
    String start_tag = "s";
    String mode_value = null;
    String count_value = null;
    String bank_value = null;
    int icount_value = 0;

    try {
      int cbank_code = CommonUtil.atoi(commonDTO.getBankCode());
      String system = tpsvcinfoDTO.getSystem_name();

      configFileName = Config.getInstance().getElement(Constants.BIZDELEGATE_TAG).getTextTrim();
      mode_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(mode_tag);
      count_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(count_tag);
      bank_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(bank_tag);

      if (cbank_code != CommonUtil.atoi(bank_value))
        return false;

      if (mode_value != null
          && count_value != null
          && mode_value.equals("on")
          && CommonUtil.CHECKisdigit(count_value, count_value.length())) {
        for (int i = 1; i <= CommonUtil.atoi(count_value); i++) {
          String tag = start_tag + 1;
          String val = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
              .getChildTextTrim(tag);
          if (val.equals(system)) {
            STF_SPerror("EFWK0011", "STF_SPsystemblock():해당시스템는 거래안됨");
            log().printf(10, this.eplevent, "Transaction Block -- system ");
            return true;
          }

        }
      }
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPsystemblock():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  private boolean STF_SPbankcodeblock() {
    String configFileName = null;
    String block_txcode_tag = "block-bankcode";
    String count_tag = "count";
    String mode_tag = "mode";
    String start_tag = "s";
    String mode_value = null;
    String count_value = null;
    int icount_value = 0;

    try {
      // 1.bankcode에대한 거래제여부를 검증한다.
      int cbankcode = CommonUtil.atoi(commonDTO.getEventNo());

      configFileName = Config.getInstance().getElement(Constants.BIZDELEGATE_TAG).getTextTrim();
      mode_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(mode_tag);
      count_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(count_tag);
      if (mode_value != null
          && count_value != null
          && mode_value.equals("on")
          && CommonUtil.CHECKisdigit(count_value, count_value.length())) {
        for (int i = 1; i <= CommonUtil.atoi(count_value); i++) {
          String tag = start_tag + 1;
          String val = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
              .getChildTextTrim(tag);
          int sv = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 1, "-"));
          int ev = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 2, "-"));
          if (cbankcode >= sv && cbankcode <= ev) {
            STF_SPerror("EFWK0011", "STF_SPbankcodeblock():해당뱅크는 거래안됨");
            log().printf(10, this.eplevent, "Transaction Block -- Bank ");
            return true;
          }
        }
      }
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPtxctl():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  private boolean STF_SPtpfqblock() {
    String configFileName = null;
    String block_txcode_tag = "block-tpfq";
    String count_tag = "count";
    String mode_tag = "mode";
    String start_tag = "s";
    String mode_value = null;
    String count_value = null;
    int icount_value = 0;

    try {
      // 1.tpfq에대한 거래제여부를 검증한다.
      int ctpfq = CommonUtil.atoi(commonDTO.getEventNo());

      configFileName = Config.getInstance().getElement(Constants.BIZDELEGATE_TAG).getTextTrim();
      mode_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(mode_tag);
      count_value = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
          .getChildTextTrim(count_tag);
      if (mode_value != null
          && count_value != null
          && mode_value.equals("on")
          && CommonUtil.CHECKisdigit(count_value, count_value.length())) {
        for (int i = 1; i <= CommonUtil.atoi(count_value); i++) {
          String tag = start_tag + 1;
          String val = XMLCache.getInstance().getXML(configFileName).getRootElement().getChild(block_txcode_tag)
              .getChildTextTrim(tag);
          int sv = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 1, "-"));
          int ev = CommonUtil.atoi(CommonUtil.catchSTRINGseq(val, 2, "-"));
          if (ctpfq >= sv && ctpfq <= ev) {
            STF_SPerror("EFWK0011", "STF_SPbankcodeblock():해당뱅크는 거래안됨");
            log().printf(10, this.eplevent, "Transaction Block -- tpfq ");
            return true;
          }
        }
      }
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      STF_SPerror("EFWK0011", this.getClass().getName() + ".STF_SPtxctl():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return false;
    }
  }

  /*************************************************************************
   * 마감전후 구분 필드를 세운다.
   *************************************************************************
   * = 시간상 관리의 경우
   * 자동화기기 마감전후를 구분하여 마감구분 필드를 세운다.
   * 토요일 : 13시 * 평 일 : 16시30
   * 향후 이 로직을 위한 추가 로직을 구성한다.
   *************************************************************************
   * = 거래별 제어관리의 경우
   * 시스템파라미터의 상태코드가 10인경우 : 모든 트랜잭션을 허용
   * 시스템파라미터의 상태코드가 20인경우 : 모든 트랜잭션을 금지
   * 시스템파라미터의 상태코드가 50인경우 : ATM/IBANK 관련된 거래만 허용
   *
   ***********************************************************************/
  private void STF_SPeod() {
    // 임시로 마감전으로 셋팅한다
    try {
      if ((CommonUtil.Str2Int(CommonUtil.GetSysTime().substring(0, 6)) >= 170000))
        tpsvcinfoDTO.setTrclass("1");
      else
        tpsvcinfoDTO.setTrclass("0");
      return;
    } catch (Exception ex) {
      STF_SPerror("EFWK0012", this.getClass().getName() + ".STF_SPeod():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return;
    }

  }

  /*************************************************************************
   * 웹컴포넌단 - EJB 서버단과의 TIMEOUT 관리
   * 현재는 단위가 초이지만 향후 미리세컨드로 바꾸는 과정이 필요하다.
   *************************************************************************
   * 이 모듈은 TPSVCINFO의 TXTIMER을 기준으로서 관리한다
   * 즉 웹단에서 시작시간을 기초로 해서 현재시간을 기초로 해서 INTERVAL이 넘어서면
   * 다음 업무단의 로직을 처리하지 않고 바로 에러로 처리
   ************************************************************************/
  private void STF_SPwebtxtimer() {
    String psStartTimer = commonDTO.getSystemInTime();
    String psEndTimer = CommonUtil.GetSysTime();
    String ss = null;
    String mm = null;
    String ee = null;
    int startsec = 99999999;
    int endsec = 99999999;

    try {
      /***********************************************************************/
      /* 시작타임 */
      /***********************************************************************/
      ss = psStartTimer.substring(0, 2);
      mm = psStartTimer.substring(2, 4);
      ee = psStartTimer.substring(4, 6);
      startsec = CommonUtil.Str2Int(ss) * 60 * 60 + CommonUtil.Str2Int(mm) * 60 + CommonUtil.Str2Int(ee);

      /***********************************************************************/
      /* 종료타임 */
      /***********************************************************************/
      ss = psEndTimer.substring(0, 2);
      mm = psEndTimer.substring(2, 4);
      ee = psEndTimer.substring(4, 6);
      endsec = CommonUtil.Str2Int(ss) * 60 * 60 + CommonUtil.Str2Int(mm) * 60 + CommonUtil.Str2Int(ee);

      log().printf(4, (EPlatonEvent) eplevent, "WEB Timer : " + CommonUtil.Str2Int(tpsvcinfoDTO.getTx_timer()));
      log().printf(4, (EPlatonEvent) eplevent, "CUR Timer : " + (endsec - startsec));
      if (CommonUtil.Str2Int(tpsvcinfoDTO.getTx_timer()) <= (endsec - startsec))
        STF_SPerror("EFWK0014", this.getClass().getName() + ".STF_SPwebtxtimer():Transaction-timeout error");
    } catch (Exception ex) {
      STF_SPerror("EFWK0013", this.getClass().getName() + ".STF_SPwebtxtimer():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return;
    }

    return;
  }

  //////////////////////////////////////////////////////////////////////////////
  // TPMSVCINFO 정보는 STF 및 ETF BTF의 중간 중간에 끼워서 시간을 셋팅하면서 진행 해야
  // 된다. 일단 한곳에서 하는 것으로 한다.
  // 변경추가부분이다
  //////////////////////////////////////////////////////////////////////////////
  private void STF_SPsettpmsvcinfo() {
    /*
     * call_service_name
     * call_tpm_in_time
     * call_tpm_out_time
     * call_tpme_interval
     * call_tpm_stf_in_time
     * call_tpm_stf_out_time
     * call_tpm_etf_in_time
     * call_tpm_etf_out_time
     * call_tpme_service_interval
     * error_code
     * call_hostseq
     * call_orgseq
     * call_location
     */

    // service_name : 클라이언트에서 셋팅
    tpsvcinfoDTO.setSystem_date(commonDTO.getSystemDate());
    tpsvcinfoDTO.setSystemInTime(commonDTO.getSystemInTime());
    tpsvcinfoDTO.setSystemOutTime(commonDTO.getSystemOutTime());
    // tpfq : 클라이언트에서 셋팅
    // operation_name : 클라이언트에서 셋팅
    // action_name : 클라이언트에서 셋팅

    return;
  }

  private void STF_SPerror(String errorcode, String message) {
    if (tpsvcinfoDTO == null || tpsvcinfoDTO.getErrorcode() == null || tpsvcinfoDTO.getErrorcode().isEmpty()) {
      if (tpsvcinfoDTO != null) {
        tpsvcinfoDTO.setErrorcode(errorcode);
        tpsvcinfoDTO.setError_message(message);
      }
      return;
    }
    switch (tpsvcinfoDTO.getErrorcode().charAt(0)) {
      case 'I':
        tpsvcinfoDTO.setErrorcode(errorcode);
        tpsvcinfoDTO.setError_message(message);
        return;
      case 'E':
        errorcode = errorcode + "|" + tpsvcinfoDTO.getErrorcode();
        tpsvcinfoDTO.setErrorcode(errorcode);
        tpsvcinfoDTO.setError_message(message);
        return;
    }
  }

  private void STF_SPmovepacket() {
    eplevent.setTPSVCINFO(this.tpsvcinfoDTO);
    eplevent.setCommon(this.commonDTO);
    //////////////////////////////////////////////////////////////////////////
    // 각 필드에 대한 정보를 재셋팅한다.
    //////////////////////////////////////////////////////////////////////////

  }

  public String STF_SPgetbusinessdate() {
    String aa = null;
    try {
      TransactionControlDAO dao = ZpilotFwkContext.getBean(TransactionControlDAO.class);
      aa = dao.queryForBusinessDate(commonDTO.getBankCode());
      aa = dao.GetBizDate();
    } catch (Exception ex) {
      STF_SPerror("EFWK0015", this.getClass().getName() + ".STF_SPgetbusinessdate():" + ex.toString());
      log().eprintf(5, (EPlatonEvent) eplevent, ex);
      return aa;
    }
    return (aa);
  }

  public static boolean STF_SPmanageloglvel(EPlatonEvent event) {
    String configFileName = null;
    String call_operation_tag = null;
    String actionClassName = null;
    String log_system_tag = "epllogej";
    String print_mode_tag = "print-mode";
    String error_mode_tag = "error-mode";
    String key_print_mode_name_value = null;
    String key_error_mode_name_value = null;
    String key_print_mode_name = null;
    String key_error_mode_name = null;
    String system_name = event.getTPSVCINFODTO().getSystem_name();

    try {
      // epllogej.xml파일을 읽기위한 기본준비를 한다.
      configFileName = Config.getInstance().getElement(Constants.BIZDELEGATE_TAG).getTextTrim();
      call_operation_tag = log_system_tag + "-" + system_name;

      key_print_mode_name_value = XMLCache.getInstance().getXML(configFileName).getRootElement()
          .getChild(call_operation_tag).getChildTextTrim(print_mode_tag);
      key_error_mode_name_value = XMLCache.getInstance().getXML(configFileName).getRootElement()
          .getChild(call_operation_tag).getChildTextTrim(error_mode_tag);

      key_print_mode_name = call_operation_tag + "." + print_mode_tag;
      key_error_mode_name = call_operation_tag + "." + error_mode_tag;

      System.out.println("key_print_mode_name_value " + key_print_mode_name_value + ":" + key_print_mode_name);
      System.out.println("key_error_mode_name_value " + key_error_mode_name_value + ":" + key_error_mode_name);

      if (hm_system_log_level == null) {
        hm_system_log_level = new HashMap();
      }

      if (hm_system_log_level.containsKey(key_print_mode_name)) {
        String val = (String) hm_system_log_level.get(key_print_mode_name);
        val = key_print_mode_name_value;
        System.out.println("hash key_print_mode_val " + val);
      } else {
        hm_system_log_level.put(key_print_mode_name, key_print_mode_name_value);
      }

      if (hm_system_log_level.containsKey(key_error_mode_name)) {
        String val = (String) hm_system_log_level.get(key_error_mode_name);
        val = key_error_mode_name_value;
        System.out.println("hash key_error_mode_val " + val);
      } else {
        hm_system_log_level.put(key_error_mode_name, key_error_mode_name_value);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  public static String STF_SPgetloglvel(String key) {
    try {
      if (hm_system_log_level.containsKey(key)) {
        String val = (String) hm_system_log_level.get(key);
        return val;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "1";
  }

  /**
   * TPMSVCAPI로 트랜잭션 상태(TPinfo)만 점검한다.
   * commit/rollback은 TCF {@code @Transactional}(Spring)에서 수행한다.
   */
  private boolean STF_SPmiddleTransaction() {
    log().printf(4, (EPlatonEvent) eplevent, "Get Transaction Level::");

    TPMSVCAPI tpmsvc = TPMSVCAPI.getInstance();
    int tpinfomode = "usertransaction".equals(transaction_type)
        ? tpmsvc.TPinfo(this.tx)
        : tpmsvc.TPinfo();

    if (tpmsvc.isSpringManagedTransaction()) {
      log().printf(4, (EPlatonEvent) eplevent, "Transaction backend    : [Spring @Transactional]");
      log().printf(4, (EPlatonEvent) eplevent,
          "Transaction commit/rollback : [delegated to Spring TCF]");
    }

    switch (tpinfomode) {
      case -1:
        STF_SPerror("EFWK0006", this.getClass().getName() + ".STF_SPmiddle()::TPinfo() Exception");
        return false;
      case 1:
        STF_SPerror("EFWK0009", "TPinfo() Marked Rollback ");
        log().printf(4, (EPlatonEvent) eplevent, "TPinfo() Marked Rollback ");
        return false;
      case 0:
        log().printf(4, (EPlatonEvent) eplevent, "Transaction Level       : [Already Transaction started]");
        log().printf(4, (EPlatonEvent) eplevent, "Transaction TPinfo mode : [0]");
        return registerTransactionInspection(tpmsvc);
      case 6:
        log().printf(4, (EPlatonEvent) eplevent, "Transaction Level       : [Not started]");
        log().printf(4, (EPlatonEvent) eplevent, "Transaction TPinfo mode : [6]");
        if (tpmsvc.isSpringManagedTransaction()) {
          log().printf(4, (EPlatonEvent) eplevent,
              "Transaction inspect note : [Spring tx expected from TCF boundary]");
        }
        return registerTransactionInspection(tpmsvc);
      default:
        STF_SPerror("EFWK0010", "TPinfo() Nothing ");
        log().printf(4, (EPlatonEvent) eplevent, "TPinfo() Marked Nothing ");
        return false;
    }
  }

  /** tpfq=100(중계) 제외, 트랜잭션 점검 등록만 수행 (실제 begin/commit/rollback 없음) */
  private boolean registerTransactionInspection(TPMSVCAPI tpmsvc) {
    if ("100".equals(tpfq())) {
      log().printf(4, (EPlatonEvent) eplevent,
          "Transaction inspect skipped (tpfq=100 relay hop)");
      return !isErr();
    }

    log().printf(4, (EPlatonEvent) eplevent, "Transaction type      : [" + transaction_type + "]");
    if (tpmsvc.TPbegin(tpsvcinfoDTO.getTx_timer())) {
      setSTF_SPtxinfo(1);
      log().printf(4, (EPlatonEvent) eplevent, "Transaction inspect registered");
      log().printf(4, (EPlatonEvent) eplevent, "Transaction TPinspect mode : [" + giTXInfoflag + "]");
      return true;
    }

    STF_SPerror("EFWK0007", this.getClass().getName() + ".STF_SPmiddle()::transaction inspect failed");
    return false;
  }

  private String tpfq() {
    String value = tpsvcinfoDTO != null ? tpsvcinfoDTO.getTpfq() : null;
    return value != null ? value : "";
  }

  public void setSTF_SPtxinfo(int offset) {
    this.giTXInfoflag = offset;
  }

  public int getSTF_SPtxinfo() {
    return this.giTXInfoflag;
  }

  public UserTransaction getUserTransactin() {
    return this.tx;
  }

  public void setUserTransactin(UserTransaction tx) {
    this.tx = tx;
  }
}