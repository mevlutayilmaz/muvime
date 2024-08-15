# MUVİME

MUVİME, popüler film veritabanı ve keşif platformu olan The Movie Database (TMDB) API'sini kullanarak geliştirilmiş bir Android uygulamasıdır. Bu uygulama, kullanıcılara filmleri arama, keşfetme, ayrıntılı bilgilerini görüntüleme ve kişisel izleme listeleri oluşturma imkanı sunar.

## Özellikler

* **Film Arama:** Kullanıcılar, istedikleri filmleri kolayca arayabilirler.
* **Film Detayları:** Filmlerin özeti, oyuncu kadrosu, yönetmeni, puanları ve benzeri gibi detaylı bilgilere erişilebilir.
* **Oyuncu Detayları:** Oyuncuların biyografisi, filmografisi ve popülerlik puanları görüntülenebilir.
* **İzleme Listesi:** Kullanıcılar, istedikleri filmleri izleme listelerine ekleyebilir ve daha sonra izlemek için kaydedebilirler.
* **Çevrimdışı Destek:** İnternet bağlantısı olmasa bile uygulama belirli bir düzeyde çalışmaya devam eder.
* **Kullanıcı Girişi/Kayıt:** Kullanıcılar, uygulamaya kayıt olabilir ve giriş yaparak izleme listelerini kaydedebilirler.
* **Çoklu Dil Desteği:** Uygulama, Türkçe ve İngilizce dillerini destekler.

## Teknolojiler

* **Programlama Dili:** Java
* **Geliştirme Ortamı:** Android Studio
* **API:** The Movie Database (TMDB) API
* **Kütüphaneler:**
    * Retrofit: API çağrıları için
    * Gson: JSON verilerini işlemek için
    * RxJava: Asenkron işlemler için
    * Glide: Görüntü yükleme ve önbellekleme için
    * SQLite: Veritabanı işlemleri için

## Kurulum
Projeyi yerel olarak kurmak için şu adımları izleyin:

1. **Depoyu klonlayın:**
   ```bash
   git clone https://github.com/mevlutayilmaz/muvime.git

2. **Android Studio'da projeyi açın.**

3. **TMDB API anahtarınızı `MovieApiService` sınıfında belirtin.**

4. **Uygulamayı çalıştırın.**


## Kullanım

**1. Giriş ve Kayıt:**

* **Kayıt Ol:** Uygulamayı ilk kez kullanıyorsanız, "Kayıt Ol" butonuna tıklayarak bir hesap oluşturmanız gerekir. Kullanıcı adı, e-posta ve şifre girmeniz istenecektir.
* **Giriş Yap:** Kayıtlı bir kullanıcıysanız, "Giriş Yap" butonuna tıklayarak e-posta ve şifrenizle giriş yapabilirsiniz.

**2. Ana Ekran:**

* **Popüler Filmler:** Ana ekranda, güncel popüler filmlerin bir listesi gösterilir.
* **Film Arama:** Arama çubuğunu kullanarak istediğiniz filmi arayabilirsiniz.

**3. Film Detayları:**

* **Film Bilgileri:** Bir filme tıkladığınızda, filmin özeti, oyuncu kadrosu, yönetmeni, puanları ve yayın tarihi gibi detaylı bilgilere erişebilirsiniz.
* **Benzer Filmler:** İlgilendiğiniz filmin altında, benzer filmlerin bir listesi de gösterilir.

**4. Oyuncu Detayları:**

* **Oyuncu Bilgileri:** Bir oyuncunun adına tıkladığınızda, oyuncunun biyografisi, filmografisi ve popülerlik puanı gibi bilgilere ulaşabilirsiniz.

**5. İzleme Listesi:**

* **Film Ekleme:** Bir filmi izleme listenize eklemek için, film detay sayfasındaki "+" simgesine tıklayabilirsiniz.
* **Film Çıkarma:** İzleme listenizdeki bir filmi çıkarmak için, film detay sayfasındaki "-" simgesine tıklayabilirsiniz.
* **İzleme Listesini Görüntüleme:** İzleme listenizdeki filmleri görmek için, profil sayfanıza gidin ve "İzleme Listesi" sekmesine tıklayın.

**6. Hesap Ayarları:**

* **Profil Düzenleme:** Profil sayfanızda, adınızı, e-posta adresinizi ve profil fotoğrafınızı güncelleyebilirsiniz.
* **Dil Değiştirme:** Uygulama dilini Türkçe veya İngilizce olarak seçebilirsiniz.
* **Çıkış Yap:** Hesabınızdan çıkış yapmak için "Çıkış Yap" butonuna tıklayabilirsiniz.

**7. İnternet Bağlantısı:**

* **Çevrimdışı Mod:** İnternet bağlantınız yoksa, uygulama daha önce önbelleğe alınmış verileri göstermeye devam edecektir. Ancak, yeni veriler yüklenemeyecektir.

## Ekran Görüntüleri

<table style="border-spacing: 0; border-collapse: collapse; width: 100%;">
  <tr>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/17f7149c-1a5a-4299-912e-04814574a670" width="150" />
      <p style="text-align: center; font-size: 12px;">Login</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/bb967b4a-9f60-4942-9ebb-d839ca88dfa0" width="150" />
      <p style="text-align: center; font-size: 12px;">Signup</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/3dc51ea7-a652-41dd-a388-141f84246947" width="150" />
      <p style="text-align: center; font-size: 12px;">Main</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/03aeba22-f875-44f2-8c79-660a33668b16" width="150" />
      <p style="text-align: center; font-size: 12px;">Search</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/07ea9e39-94b7-4de2-9d6a-d4585f262c16" width="150" />
      <p style="text-align: center; font-size: 12px;">Detail</p>
    </td>
  </tr>

  <tr>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/1686cde9-8d9b-4e32-ac43-6cdf6e20e7ff" width="150" />
      <p style="text-align: center; font-size: 12px;">Cast</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/a7eeceb8-1785-4500-b7c5-63ead583c71f" width="150" />
      <p style="text-align: center; font-size: 12px;">Account</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/aff528a7-4d2f-483f-8795-359404549a59" width="150" />
      <p style="text-align: center; font-size: 12px;">Account</p>
    </td>
    <td style="padding: 1; vertical-align: middle; text-align: center;">
      <img src="https://github.com/user-attachments/assets/31dc1e07-6dba-4b84-9296-dafdcd8ab4b5" width="150" />
      <p style="text-align: center; font-size: 12px;">ConOff</p>
    </td>
  </tr>

  </tr>
</table>





