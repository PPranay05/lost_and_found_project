/* Real QR Scanner and Verification JS Module */

let videoStream = null;
let scanningInterval = null;

// Start Camera Stream
async function startQrCamera(videoElementId, onScanSuccess) {
  const video = document.getElementById(videoElementId);
  const laser = document.querySelector('.scanner-laser');

  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    showToast('Camera access is not supported by your browser. Please use manual entry or file upload.', 'error');
    return;
  }

  try {
    videoStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'environment' }
    });

    video.srcObject = videoStream;
    video.setAttribute('playsinline', true);
    await video.play();

    if (laser) laser.style.display = 'block';

    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');

    // Continuously capture canvas frame & decode via backend or canvas
    scanningInterval = setInterval(async () => {
      if (video.readyState === video.HAVE_ENOUGH_DATA) {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        
        const imageDataUrl = canvas.toDataURL('image/png');
        
        // Attempt fast backend decode
        try {
          const res = await fetch('/api/qr/verify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ qrCodeData: imageDataUrl })
          });
          const result = await res.json();
          if (result.valid) {
            stopQrCamera(videoElementId);
            onScanSuccess(result);
          }
        } catch (e) {
          // Keep scanning until valid QR found
        }
      }
    }, 1000);

  } catch (err) {
    showToast('Unable to access camera: ' + err.message + '. Use file upload or manual input below.', 'error');
  }
}

// Stop Camera Stream
function stopQrCamera(videoElementId) {
  if (scanningInterval) clearInterval(scanningInterval);
  if (videoStream) {
    videoStream.getTracks().forEach(track => track.stop());
    videoStream = null;
  }
  const laser = document.querySelector('.scanner-laser');
  if (laser) laser.style.display = 'none';

  const video = document.getElementById(videoElementId);
  if (video) video.srcObject = null;
}

// Decode uploaded QR Code Image file
async function handleQrFileUpload(fileInput, onScanSuccess) {
  const file = fileInput.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await fetch('/api/qr/decode', {
      method: 'POST',
      body: formData
    });
    const data = await res.json();
    if (data.status === 'SUCCESS') {
      // Now verify decoded QR text
      const verifyRes = await fetch('/api/qr/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ qrCodeData: data.decodedText })
      });
      const verifyData = await verifyRes.json();
      onScanSuccess(verifyData);
    } else {
      showToast(data.message || 'Could not decode QR code from image', 'error');
    }
  } catch (err) {
    showToast('Failed to process image file: ' + err.message, 'error');
  }
}

// Verify manually entered QR code string
async function verifyManualQrCode(qrString, claimId, onScanSuccess) {
  if (!qrString || !qrString.trim()) {
    showToast('Please enter a valid QR Code ID or payload', 'error');
    return;
  }

  try {
    const data = await fetchApi('/qr/verify', {
      method: 'POST',
      body: JSON.stringify({
        qrCodeData: qrString.trim(),
        claimId: claimId || null
      })
    });
    onScanSuccess(data);
  } catch (err) {
    // Exception handled in fetchApi
  }
}
