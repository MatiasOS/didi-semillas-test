package com.atixlabs.semillasmiddleware.app.bondarea.service;

import com.atixlabs.semillasmiddleware.app.bondarea.dto.BondareaLoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.dto.BondareaLoanResponse;
import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BondareaService {


    private LoanRepository loanRepository;

    @Autowired
    public BondareaService (LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }
    @Value("${bondarea.base_url}")
    private String serviceURL;

    @Value("${bondarea.access_key}")
    private String access_key;

    @Value("${bondarea.access_token}")
    private String access_token;

    @Value("${bondarea.idm}")
    private String idm;

    // ID de la cuenta representado
    //private String idAccount;

    // Valores separados por "|" (pipe) de las columnas a mostrar para cada registro. (Ej. sta|t|id|staInt|id_pg|pg|fOt|fPri|sv|usr|cuentasTag|dni|pp|ppt|m)
    private String loanColumns;

    // Valores separados por "|" (pipe) de los estados de préstamos a listar.  (Ej. 55|60)
   // private String loanstate;



    //contains json converter and date format configuration
    private Gson gson = null;

    //contains service link with factory
    private Retrofit retrofit = null;

    private BondareaEndpoint bondareaEndpoint = null;



    public void initializeBondareaApi() {
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(serviceURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        bondareaEndpoint = retrofit.create(BondareaEndpoint.class);


        log.info("initialize bondarea api:");

    }

    /**
     * MOCK PURPOSE
     * @return BpndareaLoanDto
     */
    private BondareaLoanDto getMockBondareaLoan(){
        BondareaLoanDto loan = new BondareaLoanDto();
        loan.setIdBondareaLoan("1L");
        loan.setDni(123456L);
        loan.setStatusName("Activo");
        loan.setAmount((float) 1000);
        loan.setExpiredAmount((float) 0);
        loan.setStatus(55);

        return loan;
    }


    /**
     * MOCK PURPOSE
     * @param idAccount
     * @param loanState
     * @return
     */
    public List<BondareaLoanDto> getMockLoans(String idAccount, String loanState){
        List<BondareaLoanDto> loans = new ArrayList<>();

        BondareaLoanDto loan = getMockBondareaLoan();
        loans.add(loan);

        BondareaLoanDto loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("2L");
        loans.add(loan2);

        BondareaLoanDto loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("3L");
        loans.add(loan3);

        BondareaLoanDto loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("4L");
        loans.add(loan4);

        return loans;
    }

    /**
     * MOCK PURPOSE
     * @param idAccount
     * @param loanState
     * @return
     */
    public List<BondareaLoanDto> secondLoansDataAllNew(String idAccount, String loanState){
        List<BondareaLoanDto> loans = new ArrayList<>();

        //id 1 is deleted

        //loan 2 is the same
        BondareaLoanDto loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("2L");
        loans.add(loan2);

        //loan 3 modified tag
        BondareaLoanDto loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("3L");
        loan3.setTagBondareaLoan("nuevo tag");
        loans.add(loan3);

        //loan 4 is in default
        BondareaLoanDto loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("4L");
        loan4.setExpiredAmount((float) 100);
        loans.add(loan4);

        //new loan
        BondareaLoanDto loan5 = getMockBondareaLoan();
        loan5.setIdBondareaLoan("5L");
        loans.add(loan5);

        return loans;
    }

    public List<BondareaLoanDto> getLoans(String idAccount, String loanState) throws Exception {

        initializeBondareaApi();
        log.info("getBondareaLoans:");

        //Fill url params
        loanColumns = "sta|t|id|staInt|id_pg|pg|fOt|fPri|sv|usr|cuentasTag|dni|pp|ppt|m";


        Call<BondareaLoanResponse> callSync = bondareaEndpoint.getLoans("comunidad","wspre", "prerepprestamos", access_key, access_token, idm, /*idAccount,*/ loanColumns, loanState);

        BondareaLoanResponse bondareaLoanResponse;
        try {
            Response<BondareaLoanResponse> response = callSync.execute();

            if (response.code() == 200) {
                bondareaLoanResponse = response.body();
                log.info("Response body " + response.body());
            }
            else
            {
                return Collections.emptyList();
            }

        }catch(JsonSyntaxException  ex){
            log.error(" Bondarea retrieved object does not match with model ", ex);
            throw new Exception("retrived object does not match with model ");
        }
        catch (SocketTimeoutException ex){
                log.error(" Bondarea timeout ", ex);
             throw new Exception("Tiemout, please try again");
            }

        catch(Exception ex){
            log.error("Bondarea error ", ex);
            throw new Exception("Error, please try again");
        }

        return bondareaLoanResponse.getLoans();

    }

   


    public void updateExistingLoans(List<Loan> newLoans) {
        //List<Loan> existingLoans = loanRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime updateTime = new DateUtil().getLocalDateTimeNowWithFormat(formatter);

        //update or create loans
        for (Loan loanToSave : newLoans) {
            loanToSave.setModifiedTime(updateTime);
            //if the newLoan existed previously -> update. Else create.
            Optional<Loan> opLoanToUpdate = loanRepository.findByIdBondareaLoan(loanToSave.getIdBondareaLoan());
            //There is a previous loan
            if (opLoanToUpdate.isPresent()) {
                //update
                Loan loanToUpdate = opLoanToUpdate.get();
                loanToUpdate.merge(loanToSave);
                loanRepository.save(loanToUpdate);
            } else {
                //create
                loanRepository.save(loanToSave);
            }
        }

        List<Loan> loansToReview = loanRepository.findAllByModifiedTimeNotAndModifiedTimeNotNull(updateTime);
        for (Loan existingLoan : loansToReview) {
            //this loan has been deleted on the new list, so this loan needs to be checked (is eliminated or is terminated)
            existingLoan.setPending(true);
            loanRepository.save(existingLoan);
        }

    }






}
