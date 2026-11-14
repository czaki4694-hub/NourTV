# NourTV

مشروع بداية لتطبيق أندرويد لمشاهدة القنوات المغربية والعربية، مع واجهة داكنة ومفضلة وبحث ومشغل HLS/M3U8، إضافة إلى لوحة إدارة ويب.

## المتطلبات
- Android Studio حديث يدعم Android Gradle Plugin 8.7.3
- JDK 17
- Android SDK 35
- Node.js 20+ للوحة الإدارة

## تشغيل تطبيق Android
1. افتح مجلد المشروع `NourTV` في Android Studio.
2. انتظر مزامنة Gradle.
3. شغّل التطبيق على جهاز أو محاكي Android.
4. أضف `google-services.json` فقط بعد إعداد مشروع Firebase (اختياري في هذه النسخة الأولية).

## تشغيل لوحة الإدارة
```bash
cd web-admin
npm install
cp .env.example .env
# ضع إعدادات Firebase الخاصة بمشروعك في .env
npm run dev
```

## ربط Firebase
أنشئ مشروع Firebase ثم فعّل Firestore وAuthentication (Email/Password).
أضف إعدادات Firebase إلى `web-admin/.env`. تطبيق Android في هذه النسخة يعرض بيانات تجريبية محلية؛ ربط Firestore في التطبيق هو الخطوة التالية بعد إنشاء مشروع Firebase وإضافة إعداداته.

**مهم:** لا تضع مفاتيح Admin SDK أو أسرار الخدمة في تطبيق Android أو في الواجهة الأمامية. حماية لوحة الإدارة يجب أن تعتمد على Firebase Authentication وFirestore Security Rules مع صلاحية `admin` موثقة من الخادم/Custom Claims. لا تعتبر إخفاء رابط لوحة الإدارة حماية كافية.

## بنية البيانات المقترحة في Firestore
Collection: `channels`
```json
{
  "name": "اسم القناة",
  "category": "Moroccan",
  "streamUrl": "https://example.com/live.m3u8",
  "logoUrl": "https://example.com/logo.png",
  "enabled": true
}
```

## روابط البث
لا توجد روابط قنوات حقيقية مضمّنة. أضف فقط روابط بث تملك حق استخدامها أو لديك تصريح ببثها. روابط المثال داخل التطبيق تجريبية وقد لا تعمل.

## البناء والنشر
- افتح المشروع في Android Studio وابنِ APK من Build > Build Bundle(s) / APK(s).
- GitHub Actions موجود في `.github/workflows/android.yml` للتحقق من بناء المشروع عند push أو pull request.
- لم يتم تضمين ملف `gradle-wrapper.jar`؛ Android Studio يستطيع إنشاء/تحديث Gradle Wrapper من واجهة Gradle، أو نفّذ `gradle wrapper` بعد تثبيت Gradle محلياً.
