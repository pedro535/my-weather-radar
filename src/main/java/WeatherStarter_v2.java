import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * demonstrates the use of the IPMA API for weather forecast
 */
public class WeatherStarter_v2 {

    //todo: should generalize for a city passed as argument
    private static int CITY_ID_AVEIRO = 1010500;

    public static void  main(String[] args ) {

        if (args.length > 0) {
            for (String city: args)
                getWeather(Integer.parseInt(city));
        }
        else
            getWeather(CITY_ID_AVEIRO);


    }

    public static void getWeather(int cityCode) {
        // get a retrofit instance, loaded with the GSon lib to convert JSON into objects
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ipma.pt/open-data/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // create a typed interface to use the remote API (a client)
        IpmaService service = retrofit.create(IpmaService.class);
        // prepare the call to remote endpoint
        Call<IpmaCityForecast> callSync = service.getForecastForACity(cityCode);

        try {
            Response<IpmaCityForecast> apiResponse = callSync.execute();
            IpmaCityForecast forecast = apiResponse.body();

            if (forecast != null) {

                ListIterator<CityForecast> iter = forecast.getData().listIterator();

                System.out.printf("\n\n--------- Previsão de 5 dias para %d ---------\n", cityCode);

                int dayNum = 1;
                while (iter.hasNext()) {
                    CityForecast info = iter.next();
                    System.out.printf( "Day %d (%s) --> max tmp: %4.1f ºC%n",
                            dayNum,
                            info.getForecastDate(),
                            Double.parseDouble(info.getTMax()));

                    dayNum++;
                }

            } else {
                System.out.println( "No results for this request!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}