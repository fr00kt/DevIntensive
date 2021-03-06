package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private ImageView mUserAvatar;
    private NavigationView mNavigationView;

    private int mCurrentEditMode = 0;

    private ImageView mCallImg;
    private ImageView mOpenGitImg;
    private ImageView mSendMailImg;
    private ImageView mOpenVkImg;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ImageView mProfileImage;

    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserBio;
    private List<EditText> mUserInfoViews;

    private TextView mUserValueRating, mUserValueCodeLine, mUserValueProjects;
    private List<TextView> mUserValueViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mUserAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_avatar);

        mCallImg = (ImageView) findViewById(R.id.call_img);
        mSendMailImg = (ImageView) findViewById(R.id.send_img);
        mOpenGitImg = (ImageView) findViewById(R.id.open_git_img);
        mOpenVkImg = (ImageView) findViewById(R.id.open_vk_img);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVk = (EditText) findViewById(R.id.vk_et);
        mUserGit = (EditText) findViewById(R.id.git_et);
        mUserBio = (EditText) findViewById(R.id.about_me_et);

        mUserValueRating = (TextView) findViewById(R.id.user_info_rait_txt);
        mUserValueCodeLine = (TextView) findViewById(R.id.user_info_code_line_txt);
        mUserValueProjects = (TextView) findViewById(R.id.user_info_project_txt);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLine);
        mUserValueViews.add(mUserValueProjects);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        mCallImg.setOnClickListener(this);
        mSendMailImg.setOnClickListener(this);
        mOpenGitImg.setOnClickListener(this);
        mOpenVkImg.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo)
                .into(mProfileImage);

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .transform(new CircleTransform())
                .into(mUserAvatar);

        if (savedInstanceState == null){
            //активити запускается впервые

        } else {
            //активити уже создавалось
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    /**
     * обрабатывает нажатие кнопки Home. для вызова NavigationDrawer
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод вызывается при старте активити перед моментом того как UI станет достепен пользователю
     * как правило в данном методе происходит регистрация подписки на событиея остановка которых была
     * произведена в onStop()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Метод вызывается когда активити становится доступен пользователю для взаимодействия
     * в данном методе как правило происходит запуск анимаций/аудио/видео/запуск BroadcastReceiver
     * необходимых для реализации UI логика/запуск выполнение потоков и т.д.
     * метод должен быть максимально легковесным для максимальной отзывчивости UI
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Метод вызывается когда текущая активити теряет фокус но остается видимой(всплытие
     * диалогового окна/частичное перекрытие другой активити и т.д)
     * <p/>
     * в данном методе реализуют сохранение легковесных UI данных/анимаций/аудио/видео  и т.д
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    /**
     * Метод вызывается когда активити становится невидимым для пользователя.
     * в данном методе происходит отписка от событий, остановка сложных анимаций, сложные операции по
     * сохранению данных/прерывание запущенных потоков и т.д.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * Метод вызывается при окончании работы активити (когда это происходит системно или после вызова
     * метода finish())
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Метод вызывается при рестарте активити/возобнавлении работы после вызова метода onStop()
     * в данном методе реализуется спецификация бизнес логики которая должна быть реализована именно
     * при рестарте активности - например запрос к серверу который необходимо вызвать при возвращении
     * из другой активности (обновление данных, подписка на определенное событие проинициализированное
     * на другом экране/специфическая бизнес логика завязанная именно на перезапуске активити)
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Обработчик нажатий
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                if (mCurrentEditMode == 0){
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.profile_placeholder:
                //  TODO: 07.07.2016 сделать выбор откуда загрузить фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_img:
                if (mCurrentEditMode == 0) {
                    callPhone(mUserPhone.getText().toString());
                }
                break;
            case R.id.send_img:
                if (mCurrentEditMode == 0) {
                    sendMail(mUserMail.getText().toString());
                }
                break;
            case R.id.open_git_img:
                if (mCurrentEditMode == 0) {
                    browseUrl("https://" + mUserGit.getText().toString());
                }
                break;
            case R.id.open_vk_img:
                if (mCurrentEditMode == 0) {
                    browseUrl("https://" + mUserVk.getText().toString());
                }
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * вспомогательный метод для инициализации(запуска) Snackbar
     * @param message любое стринговое значение которое будет отображатся в Snackbar
     */
    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * метод инициализирует(устанавливает) на ActionBar кнопку home
     */
    private void setupToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams =(AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * метод инициализирует обработчик события  OnNavigationItemSelectedListener на mNavigationView
     * и обрабатывает событие (выполняет действие)
     */
    private void setupDrawer(){
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    mSelectedImage = data.getData();
                    Log.d(TAG, "REQUEST_GALLERY_PICTURE");
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    Log.d(TAG, "REQUEST_CAMERA_PICTURE");
                    insertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * переключает режим редактирования
     * @param mode если 1 режим редактирования, если 0 режим просмотра
     */
    private void changeEditMode(int mode){
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {

                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {

                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserFields();
            }
        }
    }

    /**
     *  метод для загрузки данных о пользователя из PreferencesManager
     */
    private void initUserFields(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void initUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValue();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * метод для записи данных пользователя в PreferencesManager
     */
    private void saveUserFields(){
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Обрабатывает нажатие клавиш
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // позаботьтесь о том, чтоб этот метод был вызван на ранних версиях платформы, в которых он (метод) не существует
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * обрабатывает нажатие кнопки назад
     */
    @Override
    public void onBackPressed() {
        //это будет вызвано или автоматически на версии 2.0 и поздних версиях, или кодом выше для ранних версий платформы
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)){
            mNavigationDrawer.closeDrawer(GravityCompat.START);
            Log.d(TAG, "Drawer opened");
        } else {
            Log.d(TAG, "Drawer closed");
            super.onBackPressed();
        };
    }

    /**
     * загрузка фотографии из галлереи
     */
    private void loadPhotoFromGallery(){
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    /**
     * загрузка фото с камеры
     */
    private void loadPhotoFromCamera(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 07.07.2016 обработать ошибку
            }

            if (mPhotoFile != null) {
                // TODO: 07.07.2016 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // TODO: 08.07.2016 тут обрабатываем разрешение (разрешение получено)
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // TODO: 08.07.2016 тут обрабатываем разрешение (разрешение получено)
            }
        }


    }

    /**
     * прячет картику профиля
     */
    private void hideProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * показывает картику профиля
     */
    private void showProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * блокирует Toolbar
     */
    private void lockToolbar(){
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * разблокирует Toolbar
     */
    private void unlockToolbar(){
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                                        getString(R.string.user_profile_dialog_camera),
                                        getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                // TODO: 07.07.2016 загрузить из галереи
                                loadPhotoFromGallery();
                                //showSnackbar("загрузить из галереи");
                                break;
                            case 1:
                                // TODO: 07.07.2016 загрузить из камеры
                                loadPhotoFromCamera();
                                //showSnackbar("загрузить из камеры");
                                break;
                            case 2:
                                // TODO: 07.07.2016 отмена
                                dialogInterface.cancel();
                                showSnackbar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    /**
     * создает файл катринки
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /**
     * Вставляет картинку профиля
     * @param selectedImage выбранная картинка
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }


    /**
     * открывает настройки программы
     */
    public void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * Инициирует телефонный звонок на заданный номер
     * @param phoneStr номер телефона
     */
    private void callPhone(String phoneStr) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneStr));
        startActivity(dialIntent);
    }
    /**
     * Инициирует отправку письма по электронной почте
     * @param email адрес электронной почты
     */
    private void sendMail(String email) {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        String  mail = Uri.parse(email).toString();
        String[] sendmail = {mail};
        mailIntent.putExtra(Intent.EXTRA_EMAIL  , sendmail);

       startActivity(Intent.createChooser(mailIntent, "Send Email"));
        try {
            startActivity(Intent.createChooser(mailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("There are no email clients installed.");
        }
    }

    /**
     * Открывает ссылку в браузере по заданному URL-адресу
     * @param url URL-адрес
     */
    private void browseUrl(String url) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browseIntent);
    }

}
