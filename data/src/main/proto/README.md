#### Install protobuf on Mac OS

```
brew install swift-protobuf
brew install nghttp2 // if required
brew install pkg-config // if required
```
```
git clone https://github.com/grpc/grpc-swift.git
cd grpc-swift
make
sudo cp protoc-gen-swift protoc-gen-swiftgrpc /usr/local/bin
```
```
bash compileSwiftProto.sh
```