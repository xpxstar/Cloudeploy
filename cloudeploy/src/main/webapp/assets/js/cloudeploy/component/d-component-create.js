var create = ({
    upfileKey:'',
    init : function () {
        // $("#addCustomComponentModal").on("hidden", function () {
        //     $(this).removeData("modal");
        //     $(this).children().remove();
        // });

        $('.li-disable').unbind();


        $('.btn-back').on('click', function () {
            $("#wizard").bwizard("back");
        });
        $('#repeatable').on('change',function(){
            var repeat = $(this).val();
            
            if ('true' == repeat) {
                $('#identy').removeClass('hide');
            } else{
                $('#identy').addClass('hide');
            };
        });
        $('#createOpAction').on('click', function () {
            var id = $('#addCustomComponentModal').attr('comid');
            var display_name = $('#com_display_name').val();
            var name = $('#com_name').val();
            var belong = $('#belong').val();
            var repeatable = $('#repeatable').val();
            var identify = $('#identify').val();
            if ('false' == repeatable) {
                identify = '';
            } else{
                alert('请填写重复标识');
                return;
            }
            if ('' != display_name && '' !=name && 0 !=belong) {
               create.createCom(id,name,display_name,belong,repeatable,identify);
            }else{
                alert('不可为空')
            }
        });

        $('#upload-btn').click(function() {
            $("#file-upload").uploadify('upload');
        });
        create.initUploadify(create.addFile);
        $('#close-file-alert').click(function(){
            $('#file-error').addClass('hide');
        });
    },
    /**
     *初始化Puppet model文件上传
     * 上传类型规定为zip，大小在4Mb以下（可更改）
     *
     */
    initUploadify : function(uploadCallBack) {
        var uploadifyOptions = {
            'auto' : true,
            'multi' : false,
            'fileTypeExts' :'*.zip',
            'fileTypeDesc' :'请选择zip文件',
            'fileSizeLimit' : '4MB', 
            'uploadLimit' : 1,
            'buttonText' : '选择文件',
            'swf' : dURIs.swfs + '/uploadify.swf',
            'uploader' : dURIs.filesURI+'/puppet',
            'fileObjName' : 'file',
            'removeCompleted' : false,
            'formData' : {
                "d-token" : getToken()
            },
            'onUploadSuccess' : function(file, data, response) {
                data = $.parseJSON(data);
                if (verifyParam(uploadCallBack)) {
                    uploadCallBack(data.fileKey, $("#com_name")
                            .val());
                }
            },
            'onSelect' : function(file) {
                var dot = file.name.lastIndexOf('.');
                var fileExtend=file.name.substring(dot).toLowerCase();
                if ('.zip' != fileExtend) {
                    $('#file-error').removeClass('hide');
                    $('#file-upload').uploadify('cancel');
                }else{
                    $("#com_name").val(file.name.substring(0,dot).toLowerCase());
                }
            }
        };
        $('#file-upload').uploadify(uploadifyOptions);
    },
    /*上传文件完成后激活创建Component的按钮
    */
    addFile : function(fileKey, fileName) {
        create.upfileKey =  fileKey;
        $('#upload-btn').hide();
        $("#createOpAction").removeClass("disabled");
    },
    /*创建Component
    */
    createCom:function(id,name,displayName,typeId,repeatable,identify){
        if (0==id) {
            ajaxPostJsonAuthc(dURIs.componentURI+'/custom', {
                    id : id,
                    name : name,
                    displayName : displayName,
                    typeId:typeId,
                    repeatable:repeatable,
                    identify:identify
                },create.createComCallBack, null, false);
        } else{
            create.createComCallBack(id);
        };
    },
    /*创建完成后进行解析，创建Operation（Action）
    */
    createComCallBack : function(data){
        $('#addCustomComponentModal').attr('comid',data);
       create.createOperation(data);
       
    },
    /*
        创建Operations
    */
    createOperation: function(comid){
        ajaxPostJsonAuthc(dURIs.operationURI+'/extract', {
                    filekey : create.upfileKey,
                    comid : comid
                },create.createOperationCallBack, null, true);
    },
    /*创建Operations完成后隐藏面板，进入编辑参数设置。
    */
    createOperationCallBack:function(data){
        $('#addCustomComponentModal').modal('hide');
        componentPanel.initCustomComponents(true);
    }
});	