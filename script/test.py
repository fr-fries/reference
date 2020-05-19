import urllib2, json, time, sys, traceback

host = 'http://localhost:10080/'

user_id = 'userid0001'
app_id = 'app_normal'
cid = 'cid_normal'
content_length = 10
content_type = 'text/plain'
upload_data = '0123456789'
response_filename = 'text.txt'

object_id = ''
upload_url = ''
upload_url_expires = 0
download_url = ''
download_url_expires = 0

def success():
    sys.exit(0)

def fail():
    sys.exit(1)

def main():
    create_upload_url = host + 'sign/v1/objects?cid=%s' % (cid)
    start_time = long(time.time() * 1000)

    try:
        req = urllib2.Request(create_upload_url)
        req.add_header('x-su-user-id', user_id)
        req.add_header('x-su-app-id', app_id)
        req.add_header('x-su-content-length', content_length)
        req.add_header('x-su-content-type', content_type)
        req.get_method = lambda: 'POST'
        print("  create upload url - url: %s, user_id: %s, app_id: %s" % (create_upload_url, user_id, app_id))
        rsp = urllib2.urlopen(req)
        rst = rsp.read()

        end_time = long(time.time() * 1000)

        js = json.loads(rst)
        object_id = js['object_id']
        upload_info = js ['upload_info']
        upload_url = upload_info['url']
        upload_url_expires = upload_info['expires']
        print('  create upload url - object_id: %s, upload_url: %s, expires: %d, elapsed: %d' % (object_id, upload_url, upload_url_expires, end_time - start_time))
        if object_id is '' or upload_url is '' or upload_url_expires is 0:
            fail()

    except Exception as e:
        if hasattr(e, 'read'):
            err_rsp = json.loads(e.read())
            rcode = err_rsp['rcode']
            rmsg = err_rsp['rmsg']

            print('  create upload url - rcode: ' + str(rcode) + ', rmsg: ' + rmsg)
        else:
            print ('  create upload url - error: ' + str(e))

        fail()

    try:
        req = urllib2.Request(upload_url, upload_data)
        req.add_header('content-length', content_length)
        req.add_header('content-type', content_type)
        req.get_method = lambda: 'PUT'
        print('  upload data - url: %s, content-length: %d, content-type: %s' % (upload_url, content_length, content_type))

        rsp = urllib2.urlopen(req)
    except Exception as e:
        print ('  error: ' + str(e))

        fail()

    get_download_url = host + 'sign/v1/objects/%s/signed?cid=%s' % (object_id, cid)
    start_time = long(time.time() * 1000)
    try:
        req = urllib2.Request(get_download_url)
        req.add_header('x-su-user-id', user_id)
        req.add_header('x-su-app-id', app_id)
        req.add_header('x-su-response-filename', response_filename)
        print("  get download url - url: %s, user_id: %s, app_id: %s, response_filename: %s" % (get_download_url, user_id, app_id, response_filename))

        rsp = urllib2.urlopen(req)
        rst = rsp.read()

        end_time = long(time.time() * 1000)

        js = json.loads(rst)
        object_id = js['object_id']
        download_info = js ['download_info']
        download_url = download_info['url']
        download_url_expires = download_info['expires']
        print('  get download url - object_id: %s, download_url: %s, expires: %d, elapsed: %d' % (object_id, download_url, download_url_expires, end_time - start_time))
        if object_id is '' or upload_url is '' or upload_url_expires is 0:
            fail()

    except Exception as e:
        if hasattr(e, 'read'):
            err_rsp = json.loads(e.read())
            rcode = err_rsp['rcode']
            rmsg = err_rsp['rmsg']

            print('  get download url - rcode: ' + str(rcode) + ', rmsg: ' + rmsg)
        else:
            print('  get download url - error: ' + str(e))

        fail()

    try:
        req = urllib2.Request(download_url)

        rsp = urllib2.urlopen(req)
        rst = rsp.read()
        print("  download data - response body: %s" % (rst))

        if rst != upload_data:
            fail()

    except Exception as e:
        print('  error: ' + str(e))

        fail()

    success()

if __name__ == '__main__':
    main()